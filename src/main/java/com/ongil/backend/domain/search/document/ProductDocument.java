package com.ongil.backend.domain.search.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.Setting;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Document(indexName = "products")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setting(settingPath = "elasticsearch/settings.json")
public class ProductDocument {

	@Id
	private Long id;

	@MultiField(
		mainField = @Field(type = FieldType.Text, analyzer = "nori_analyzer"),
		otherFields = {
			@InnerField(suffix = "autocomplete", type = FieldType.Text, analyzer = "autocomplete_analyzer")
		}
	)
	private String name;

	@Field(type = FieldType.Text, analyzer = "nori_analyzer")
	private String colors;

	@MultiField(
		mainField = @Field(type = FieldType.Text, analyzer = "nori_analyzer"),
		otherFields = {
			@InnerField(suffix = "autocomplete", type = FieldType.Text, analyzer = "autocomplete_analyzer")
		}
	)
	private String brandName;

	@MultiField(
		mainField = @Field(type = FieldType.Text, analyzer = "nori_analyzer"),
		otherFields = {
			@InnerField(suffix = "autocomplete", type = FieldType.Text, analyzer = "autocomplete_analyzer")
		}
	)
	private String categoryName;

	@Field(type = FieldType.Integer)
	private Integer price;
}
