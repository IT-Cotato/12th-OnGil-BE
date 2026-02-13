package com.ongil.backend.domain.review.service.prompter;

import org.springframework.stereotype.Component;

import com.ongil.backend.domain.review.dto.request.AiReviewGenerateRequest;

@Component
public class SizeReviewPrompter implements ReviewPrompter {

	private static final String SYSTEM_PROMPT = """
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

	@Override
	public String getSystemPrompt() {
		return SYSTEM_PROMPT;
	}

	@Override
	public String buildUserMessage(AiReviewGenerateRequest request) {
		StringBuilder prompt = new StringBuilder();
		prompt.append("의류 종류: ").append(request.getClothingType().getDisplayName()).append("\n");
		prompt.append("착용 상태: ").append(request.getSizeAnswer().getDisplayName()).append("\n");

		if (request.getSizeAnswer().isNeedsSecondaryQuestion() && !request.getFitIssueParts().isEmpty()) {
			prompt.append("불편 항목 리스트: ").append(String.join(", ", request.getFitIssueParts())).append("\n");

			prompt.append("\n[생성 지시]");
			prompt.append("\n- 위 리스트에 나열된 각 항목(예: 어깨&목, 전반적 등)마다 **반드시 2문장씩** 생성할 것.");

			if (request.getFitIssueParts().contains("전반적")) {
				prompt.append("\n- '전반적' 항목에 대해서는 특정 신체 부위 언급 없이 전체적인 실루엣이나 조이는 느낌에 대해 2문장을 생성할 것.");
			}

			prompt.append("\n- 결과적으로 총 ").append(request.getFitIssueParts().size() * 2).append("문장이 생성되어야 함.");
		}
		else {
			prompt.append("특이사항: 전반적으로 잘 맞고 편안함\n");
			prompt.append("[지시] 전반적인 편안함과 만족감을 강조하는 서로 다른 2문장을 생성할 것.\n");
		}

		prompt.append("\n[출력 형식 지시]");
		prompt.append("\n- 반드시 각 문장 사이를 '|' 기호로만 구분할 것.");
		prompt.append("\n- 문장 끝에 마침표를 찍지 말고 바로 '|'로 이을 것.");
		return prompt.toString();
	}
}
