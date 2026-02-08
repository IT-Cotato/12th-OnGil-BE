# Profile Picture Feature Setup Guide

## Overview

This feature allows users to upload profile pictures directly to AWS S3 storage. The implementation includes:

- Direct file upload via multipart/form-data
- Automatic validation of file type and size
- Secure storage in AWS S3
- Automatic cleanup of old profile images

## Prerequisites

### AWS S3 Setup

1. **Create an S3 Bucket**
   - Log in to AWS Console
   - Navigate to S3
   - Create a new bucket (e.g., `ongil-profile-images`)
   - Region: `ap-northeast-2` (Seoul) or your preferred region

2. **Configure Bucket Permissions**
   - Enable public read access for profile images (optional, depends on requirements)
   - Or configure CloudFront distribution for private bucket with signed URLs

3. **Create IAM User**
   - Create a new IAM user for the application
   - Attach policy with S3 permissions:
   ```json
   {
     "Version": "2012-10-17",
     "Statement": [
       {
         "Effect": "Allow",
         "Action": [
           "s3:PutObject",
           "s3:GetObject",
           "s3:DeleteObject"
         ],
         "Resource": "arn:aws:s3:::ongil-profile-images/*"
       }
     ]
   }
   ```

4. **Get Access Credentials**
   - Note down the Access Key ID and Secret Access Key

## Environment Configuration

Add the following environment variables to your `.env` file or deployment configuration:

```properties
# AWS S3 Configuration
AWS_S3_BUCKET_NAME=ongil-profile-images
AWS_S3_REGION=ap-northeast-2
AWS_S3_ACCESS_KEY=your-access-key-here
AWS_S3_SECRET_KEY=your-secret-key-here
```

### Local Development

For local development, you can:
1. Create a `.env` file in the project root with the above variables
2. Or set them as system environment variables
3. Or configure them in your IDE's run configuration

### Production Deployment

For production:
1. Use AWS Secrets Manager or Parameter Store for credentials
2. Or configure them in your deployment platform (e.g., Docker, Kubernetes secrets)
3. Never commit credentials to version control

## API Endpoints

### 1. Upload Profile Image (File Upload)

**Endpoint:** `POST /api/users/me/profile-image/upload`

**Authentication:** Required (JWT token)

**Content-Type:** `multipart/form-data`

**Request:**
```
POST /api/users/me/profile-image/upload
Authorization: Bearer {jwt-token}
Content-Type: multipart/form-data

file: [binary image file]
```

**Response:**
```json
{
  "success": true,
  "data": {
    "userId": 1,
    "email": "user@example.com",
    "name": "John Doe",
    "profileUrl": "https://ongil-profile-images.s3.ap-northeast-2.amazonaws.com/profile-images/1_abc123.jpg",
    "phone": "010-1234-5678",
    "points": 1000
  }
}
```

**Validation Rules:**
- File size: Maximum 5MB
- File types: JPEG, JPG, PNG, GIF, WEBP only
- File is required (cannot be empty)

**Error Responses:**
```json
{
  "success": false,
  "error": {
    "code": "FILE-001",
    "message": "파일이 제공되지 않았습니다."
  }
}
```

```json
{
  "success": false,
  "error": {
    "code": "FILE-002",
    "message": "파일 크기가 제한을 초과했습니다. (최대 5MB)"
  }
}
```

```json
{
  "success": false,
  "error": {
    "code": "FILE-003",
    "message": "지원하지 않는 파일 형식입니다. (jpeg, jpg, png, gif, webp만 가능)"
  }
}
```

### 2. Update Profile Image (URL-based) - Legacy

**Endpoint:** `PATCH /api/users/me/profile-image`

**Authentication:** Required (JWT token)

**Content-Type:** `application/json`

This endpoint remains for backward compatibility if you want to set a profile image URL directly.

**Request:**
```json
{
  "profileImageUrl": "https://example.com/image.jpg"
}
```

## Client Integration Examples

### JavaScript (Fetch API)

```javascript
const uploadProfilePicture = async (file, token) => {
  const formData = new FormData();
  formData.append('file', file);

  const response = await fetch('/api/users/me/profile-image/upload', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`
    },
    body: formData
  });

  return await response.json();
};

// Usage
const fileInput = document.getElementById('profile-picture');
fileInput.addEventListener('change', async (e) => {
  const file = e.target.files[0];
  const result = await uploadProfilePicture(file, userToken);
  console.log('Uploaded:', result.data.profileUrl);
});
```

### React Example

```jsx
import { useState } from 'react';

const ProfilePictureUpload = () => {
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState(null);

  const handleUpload = async (event) => {
    const file = event.target.files[0];
    
    // Client-side validation
    if (file.size > 5 * 1024 * 1024) {
      setError('File size must be less than 5MB');
      return;
    }

    const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'];
    if (!allowedTypes.includes(file.type)) {
      setError('Only JPEG, PNG, GIF, and WebP images are allowed');
      return;
    }

    setUploading(true);
    setError(null);

    const formData = new FormData();
    formData.append('file', file);

    try {
      const response = await fetch('/api/users/me/profile-image/upload', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: formData
      });

      const result = await response.json();
      
      if (result.success) {
        console.log('Profile picture updated:', result.data.profileUrl);
        // Update UI with new profile picture
      } else {
        setError(result.error.message);
      }
    } catch (err) {
      setError('Failed to upload profile picture');
    } finally {
      setUploading(false);
    }
  };

  return (
    <div>
      <input 
        type="file" 
        accept="image/jpeg,image/jpg,image/png,image/gif,image/webp"
        onChange={handleUpload}
        disabled={uploading}
      />
      {uploading && <p>Uploading...</p>}
      {error && <p style={{ color: 'red' }}>{error}</p>}
    </div>
  );
};
```

## File Storage Structure

Profile images are stored in S3 with the following structure:

```
s3://ongil-profile-images/
  └── profile-images/
      ├── 1_abc123-def456.jpg
      ├── 2_xyz789-ghi012.png
      └── ...
```

File naming convention: `{userId}_{uuid}.{extension}`

This ensures:
- No naming conflicts
- Easy identification of user's images
- Ability to have multiple versions if needed

## Automatic Cleanup

When a user uploads a new profile picture:
1. The system checks if they have an existing S3-hosted profile image
2. If yes, the old image is automatically deleted from S3
3. The new image is uploaded
4. The database is updated with the new URL

**Note:** External URLs (non-S3) are preserved and not deleted.

## Cost Considerations

AWS S3 costs include:
- Storage: ~$0.023 per GB/month (Seoul region)
- PUT requests: ~$0.005 per 1,000 requests
- GET requests: ~$0.0004 per 1,000 requests
- Data transfer: First 1 GB/month free, then ~$0.126 per GB

For a user base of 10,000 users with 500KB average image size:
- Storage: ~5 GB × $0.023 = ~$0.12/month
- Operations: Negligible for typical usage

## Monitoring and Maintenance

### Check S3 Bucket Usage
```bash
aws s3 ls s3://ongil-profile-images/profile-images/ --summarize --human-readable --recursive
```

### Clean Up Orphaned Images (Optional)
You may want to periodically check for and remove images that are no longer referenced in the database.

## Troubleshooting

### Issue: "FILE_UPLOAD_FAILED" Error

**Possible causes:**
1. Incorrect AWS credentials
2. Missing S3 bucket permissions
3. Bucket doesn't exist
4. Wrong region specified

**Solution:**
1. Verify environment variables are set correctly
2. Check IAM user has PutObject permission
3. Verify bucket name and region
4. Check application logs for detailed error messages

### Issue: Images Not Displaying

**Possible causes:**
1. Bucket is not publicly accessible
2. CORS not configured

**Solution:**
1. Configure bucket policy for public read access, or
2. Use CloudFront distribution, or
3. Generate presigned URLs for private access

### Enable Public Access (if needed)

Bucket policy for public read:
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "PublicReadGetObject",
      "Effect": "Allow",
      "Principal": "*",
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::ongil-profile-images/profile-images/*"
    }
  ]
}
```

## Security Best Practices

1. **Never commit AWS credentials** to version control
2. **Use IAM roles** when running on AWS (EC2, ECS, Lambda)
3. **Rotate credentials** periodically
4. **Monitor S3 access logs** for suspicious activity
5. **Enable S3 versioning** for backup purposes
6. **Configure lifecycle policies** to archive or delete old versions
7. **Validate file content**, not just extension/MIME type
8. **Implement rate limiting** to prevent abuse

## Future Enhancements

Potential improvements:
1. Image resizing/optimization before upload (using AWS Lambda)
2. Multiple image sizes (thumbnail, medium, large)
3. CDN integration (CloudFront)
4. Image compression
5. Support for animated GIFs with size limits
6. Virus scanning for uploaded files
7. Watermarking
