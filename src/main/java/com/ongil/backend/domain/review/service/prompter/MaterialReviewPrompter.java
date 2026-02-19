package com.ongil.backend.domain.review.service.prompter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ongil.backend.domain.review.dto.request.AiReviewGenerateRequest;

@Component
public class MaterialReviewPrompter implements ReviewPrompter {

	private static final String SYSTEM_PROMPT = """
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

	@Override
	public String getSystemPrompt() {
		return SYSTEM_PROMPT;
	}

	@Override
	public String buildUserMessage(AiReviewGenerateRequest request) {
		StringBuilder prompt = new StringBuilder();

		boolean isPositive = request.getMaterialAnswer().isPositive();
		prompt.append("소재 평가 상태: ").append(isPositive ? "긍정적" : "부정적(아쉬움)").append("\n");
		prompt.append("소재 평가: ").append(request.getMaterialAnswer().getDisplayName()).append("\n");

		if (!request.getMaterialFeatures().isEmpty()) {
			boolean hasThicknessAll = request.getMaterialFeatures().contains("두께감:선택지전체");
			List<String> otherFeatures = request.getMaterialFeatures().stream()
				.filter(f -> !"두께감:선택지전체".equals(f))
				.toList();

			// 두께감 전체 선택 단독인 경우
			if (hasThicknessAll && otherFeatures.isEmpty()) {
				prompt.append("\n[특수 지시] 두께감만 선택됨. 아래 3가지 상황에 맞춰 각각 1문장씩 생성하되, '|' 기호로 구분할 것:\n");
				if (isPositive) {
					prompt.append("1. 두꺼워서 따뜻함 | 2. 얇아서 시원함 | 3. 적당한 두께임\n");
				} else {
					prompt.append("1. 소재가 너무 두꺼워서 답답함 | 2. 너무 얇아서 추운 느낌임 | 3. 두께가 애매해서 아쉬움\n");
				}
			} else {
				prompt.append("선택한 소재 특징:\n");
				for (String feature : otherFeatures) {
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
		} else {
			prompt.append("\n[지시] 특정 소재 속성 언급 없이, 전반적으로 무난하고 평범하다는 인상의 서로 다른 '2문장'을 생성할 것.\n");
		}

		prompt.append("\n[최종 출력 형식 지시]");
		prompt.append("\n- 모든 문장은 반드시 '|' 기호로만 구분하여 나열할 것.");
		prompt.append("\n- 마침표(.)나 줄바꿈(\n)을 구분자로 사용하지 말 것.");

		return prompt.toString();
	}
}