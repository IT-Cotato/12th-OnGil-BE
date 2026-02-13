package com.ongil.backend.domain.review.service;

import com.ongil.backend.domain.review.dto.request.AiReviewGenerateRequest;
import com.ongil.backend.domain.review.dto.response.AiReviewResponse;
import com.ongil.backend.domain.review.validator.ReviewValidator;
import com.ongil.backend.global.common.exception.AppException;
import com.ongil.backend.global.common.exception.ErrorCode;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiReviewGeneratorService {

    private final ReviewValidator reviewValidator;

    @Value("${openai.api-key}")
    private String openAiApiKey;

    private static final String SIZE_REVIEW_PROMPT = """
            너는 의류 착용 후기를 생성하는 AI임.
            사용자가 선택한 의류 종류, 부위, 강도를 기반으로
            시니어도 이해하기 쉬운 표현으로 자연스러운 후기 문장을 생성해야 함.
            
            아래 규칙을 반드시 모두 지켜야 함.
            
            ━━━━━━━━━━━━━━
            [1. 부위 범위 규칙]
            
            사용자가 선택한 부위는 문장에서 언급 가능한 유일한 불편 범위임
            선택된 부위 외의 신체 부위, 상황, 결과는 절대 언급 불가함
            복합 부위(예: 엉덩이 & 가랑이)는 하나의 묶음이지만
            문장 안에서 단일 부위만 언급했을 경우,
            불편한 상황·감정·원인은 해당 부위로만 한정해야 함
            
            예시로,
            가랑이가 답답해서 앉아 있을 때 불편함은 괜찮지만
            가랑이가 답답해서 엉덩이 봉제선이 당겨지는 느낌은 안됨
            
            복합 부위를 함께 언급한 경우에만
            두 부위 모두에 대한 불편 상황 설명 가능함
            단, 부위 간 인과 관계(원인→결과) 표현은 금지함
            
            ━━━━━━━━━━━━━━
            [2. 강도 표현 규칙]
            
            강도에 따라 아래 표현 중에서만 선택하여 사용해야 하며,
            의미를 벗어나는 과장·완화 표현은 사용 불가함
            
            너무 답답:
            매우, 심하게, 숨쉬기 힘들 정도로
            
            조금 답답:
            살짝, 신경 쓰일 정도로
            (허리&복부 / 가슴&몸통의 경우만)
            밥 먹고 나면 답답해지는 느낌임
            
            약간 커서 거슬림:
            거슬릴 정도로
            
            너무 커서 불편함:
            많이, 지나치게
            
            수선이 필요할 정도로 큼:
            입고 다니기 힘들 정도로 큼
            
            편함:
            입는 내내 편함
            신축성이 좋아 움직이기 편함
            이 경우 특정 부위 언급 없이
            전반적인 착용감만 작성해야 함
            
            ━━━━━━━━━━━━━━
            [3. 표현 톤 규칙 (시니어 친화)]
            
            전문 용어, 유행어, 신체 과장 표현 사용 금지
            아래 표현은 사용하지 않음
            (핏, 옷매무새, Y존, 라인 부각, 실루엣 등)
            
            대신 일상적이고 직관적인 표현 사용
            예:
            - 몸에 딱 붙는다
            - 움직일 때 불편하다
            - 앉거나 일어날 때 신경 쓰인다
            - 오래 입기엔 부담된다
            
            ━━━━━━━━━━━━━━
            [4. 문장 구조 규칙]
            
            하나의 문장에는 하나의 불편 경험만 포함함
            평가 + 상황 + 느낌의 순서를 유지함
            문장 끝은 반드시 ~임 으로 끝냄
            
            ━━━━━━━━━━━━━━
            [출력 목표]
            
            사용자가 선택한
            - 의류 종류
            - 부위
            - 강도
            를 정확히 반영하여
            부위 범위를 넘지 않는,
            강도에 맞는,
            시니어도 이해 가능한 한 문장 후기를 생성함.
            """;

    private static final String MATERIAL_REVIEW_PROMPT = """
            당신은 의류 소재 착용 후기를 실제 사용자 경험처럼 풀어내는 AI입니다.
            특히 시니어 사용자가 이해하기 쉬운 표현을 최우선으로 사용해야 합니다.
            
            ━━━━━━━━━━━━━━━━━━
            [기본 전제]
            본 프롬프트는 소재에 대한 후기만 작성함
            핏, 사이즈, 신체 부위, 착용 부위 언급 금지
            소재의 인상과 느낌만 다룸
            
            ━━━━━━━━━━━━━━━━━━
            [가장 중요한 규칙 – 선택 항목 일치]
            사용자가 선택한 항목만 서술해야 함
            선택하지 않은 소재 속성으로 확장 금지
            예: 촉감 선택 → 촉감에 대한 내용만 작성
            예: 무게감 선택 → 무게감 외 언급 금지
            
            좋은 점 선택 시 부정 표현 금지
            아쉬운 점 선택 시 긍정 표현 금지
            눈에 띄는 점 없음 선택 시
            → 장점·단점 분석, 속성 설명 모두 금지
            
            ━━━━━━━━━━━━━━━━━━
            [핵심 작성 원칙]
            한 문장에는 하나의 느낌만 작성
            1인칭 후기 톤 사용
            시니어가 이해하기 쉬운 말만 사용
            모든 문장 끝맺음은 반드시 "~임"
            판단, 비교, 조언, 추천, 해결책 작성 금지
            
            ━━━━━━━━━━━━━━━━━━
            [소재 속성 카테고리]
            촉감 (부드러움 / 거칠음)
            무게감 (가벼움 / 무거움)
            구김 정도 (많음 / 없음)
            두께감 (얇음 / 두꺼움)
            보풀 (있음 / 없음)
            비침 정도 (안 비침 / 비침)
            
            ━━━━━━━━━━━━━━━━━━
            [2차 질문 – 좋은 점 선택 시 출력 규칙]
            선택한 속성 중 하나만 기준으로 1~2문장 작성
            편안함, 부담 없음, 일상 사용 중심으로 표현
            
            [좋은 점 예시 가이드]
            촉감(부드러움):
            · 손에 닿는 느낌이 부드러움
            
            무게감(가벼움):
            · 가벼워서 편안함
            
            구김 없음:
            · 오래 입어도 구김이 잘 생기지 않음
            
            두께감 두꺼움/얇음:
            · 두꺼워서 따뜻함 / 얇아서 시원함 / 봄, 가을에 입기 적당한 두께
            
            보풀 없음:
            · 보풀이 잘 안나는 소재
            
            비침 없음:
            · 안이 비치지 않아 신경 쓰이지 않음
            
            ━━━━━━━━━━━━━━━━━━
            [2차 질문 – 아쉬운 점 선택 시 출력 규칙]
            선택한 속성 하나만 기준으로 1~2문장 작성
            불편하지만 과장 없이, 체감 위주로 표현
            
            [아쉬운 점 예시 가이드]
            촉감(거칠음):
            · 피부에 닿을 때 거칠음
            
            무게감(무거움):
            · 무거워서 오래 입기엔 부담됨
            
            구김 많음:
            · 조금만 움직여도 구김이 생김
            
            두꺼움:
            · 두꺼워서 더움 / 얇아서 추움
            
            보풀 있음:
            · 보풀이 금방 생기는 소재임
            
            비침 있음:
            · 안이 비쳐 보여 신경 쓰임
            
            ━━━━━━━━━━━━━━━━━━
            [③ 눈에 띄는 점은 없었어요 선택 시 전용 규칙]
            소재 속성(촉감, 무게, 두께 등) 언급 금지
            장점·단점 분석 금지
            "무난함 / 평범함 / 거슬리지 않음" 인상만 전달
            
            아래 문장 유형 중 1~2문장만 출력
            [허용 문장 예시]
            전반적으로 무난해서 부담 없이 입을 수 있음
            특별히 좋거나 아쉬운 점 없이 평범한 느낌임
            특별히 좋은 점은 없지만 입는 데 거슬리지도 않았음
            일상적으로 입는 데에 무리가 없는 평범한 소재임
            
            ━━━━━━━━━━━━━━━━━━
            [출력 분량 규칙]
            모든 선택지: 1~2문장
            문장 간 의미 중복 금지
            규칙 위반 시 잘못된 출력으로 간주됨
            """;

    public AiReviewResponse generateSizeReview(AiReviewGenerateRequest request) {
        reviewValidator.validateReviewStepCompletion(request.getSizeAnswer(), request.getFitIssueParts());
        OpenAiService service = new OpenAiService(openAiApiKey);
        String userMessage = buildSizeReviewPrompt(request);

        String aiResponse = callOpenAi(service, MATERIAL_REVIEW_PROMPT, userMessage);

        List<String> reviewList = Arrays.stream(aiResponse.split("\\|"))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .toList();

        return AiReviewResponse.of(request.getReviewId(), reviewList);
    }

    public AiReviewResponse generateMaterialReview(AiReviewGenerateRequest request) {
        reviewValidator.validateReviewStepCompletion(request.getMaterialAnswer(), request.getMaterialFeatures());
        OpenAiService service = new OpenAiService(openAiApiKey);
        String userMessage = buildMaterialReviewPrompt(request);

        String aiResponse = callOpenAi(service, MATERIAL_REVIEW_PROMPT, userMessage);

        List<String> reviewList = Arrays.stream(aiResponse.split("\\|"))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .toList();

        return AiReviewResponse.of(request.getReviewId(), reviewList);
    }

    private String buildSizeReviewPrompt(AiReviewGenerateRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("의류 종류: ").append(request.getClothingType().getDisplayName()).append("\n");
        prompt.append("착용 상태: ").append(request.getSizeAnswer().getDisplayName()).append("\n");

        if (request.getSizeAnswer().isNeedsSecondaryQuestion() && !request.getFitIssueParts().isEmpty()) {
            prompt.append("불편 부위: ").append(String.join(", ", request.getFitIssueParts())).append("\n");
            prompt.append("\n[특수 지시]");
            prompt.append("- 위 리스트에 있는 각 '복합 부위' 항목 전체를 소재로 하여 서로 다른 2문장씩 생성할 것.\n");
            prompt.append("- 예: '가슴&몸통' 선택 시 -> 가슴과 몸통 전체의 착용감을 다루는 문장 2개 생성.\n");
        } else {
            prompt.append("특이사항: 전체적으로 편안함\n");
            prompt.append("[지시] 전반적인 편안함을 강조하는 서로 다른 2문장을 생성할 것.\n");
        }

        prompt.append("\n[출력 형식] 반드시 각 문장 사이를 '|' 기호로만 구분하여 출력할 것.");
        return prompt.toString();
    }

    private String buildMaterialReviewPrompt(AiReviewGenerateRequest request) {
        StringBuilder prompt = new StringBuilder();

        boolean isPositive = request.getMaterialAnswer().isPositive();
        prompt.append("소재 평가 상태: ").append(isPositive ? "긍정적" : "부정적(아쉬움)").append("\n");
        prompt.append("소재 평가: ").append(request.getMaterialAnswer().getDisplayName()).append("\n");

        if (!request.getMaterialFeatures().isEmpty()) {
            prompt.append("선택한 소재 특징:\n");
            boolean hasThicknessAll = false;

            for (String feature : request.getMaterialFeatures()) {
                if ("두께감:선택지전체".equals(feature)) {
                    hasThicknessAll = true;
                    continue;
                }
                prompt.append("- ").append(feature).append("\n");
            }

            prompt.append("\n[문장 생성 규칙]");
            prompt.append("\n1. 위 리스트에 나열된 각 특징마다 서로 다른 느낌의 '2문장씩'을 반드시 생성할 것.");

            if (hasThicknessAll) {
                prompt.append("\n[특수 지시] 두께감은 아래 3가지 상황에 맞춰 생성하되, 각 문장 사이를 '|' 기호로 구분할 것:\n");
                if (isPositive) {
                    prompt.append("1. 두꺼워서 따뜻함 | 2. 얇아서 시원함 | 3. 적당한 두께임\n");
                } else {
                    prompt.append("1. 소재가 너무 두꺼워서 답답함 | 2. 너무 얇아서 추운 느낌임 | 3. 두께가 애매해서 아쉬움\n");
                }
            }
        }
        else {
            prompt.append("\n[지시] 특정 소재 속성 언급 없이, 전반적으로 무난하고 평범하다는 인상의 서로 다른 '2문장'을 생성할 것.\n");
        }

        prompt.append("\n[최종 출력 형식 지시]");
        prompt.append("\n- 모든 문장은 반드시 '|' 기호로만 구분하여 나열할 것.");
        prompt.append("\n- 문장 끝은 반드시 '~임'으로 끝낼 것.");
        prompt.append("\n- 마침표(.)나 줄바꿈(\n)을 구분자로 사용하지 말 것.");

        return prompt.toString();
    }

    private String callOpenAi(OpenAiService service, String systemPrompt, String userMessage) {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), systemPrompt));
        messages.add(new ChatMessage(ChatMessageRole.USER.value(), userMessage));

        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
            .model("gpt-4o-mini")
            .messages(messages)
            .temperature(0.7)
            .build();

        try {
            return service.createChatCompletion(completionRequest)
                .getChoices().get(0).getMessage().getContent().trim();
        } catch (Exception e) {
            log.error("AI 생성 실패: ", e);
            throw new AppException(ErrorCode.AI_GENERATION_ERROR);
        }
    }
}
