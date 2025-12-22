# CostConfig API 테스트 가이드

## 🎯 기능 요약
- **invitecost, reviewcost, chargecostrate** 설정값을 API로 조회/수정 가능
- **값 수정 시 해당 등급의 모든 기존 Cost가 자동으로 재계산됨**

## 📋 API 엔드포인트

### 1. 모든 설정 조회 (GET)
```bash
GET /cost-config
```

**응답 예시:**
```json
[
  {
    "configId": 1,
    "configType": "MEMBER_GRADE_CERTIFICATION",
    "gradeName": "level1",
    "value": 2000000,
    "createdat": "2025-12-22T10:50:09",
    "updatedat": "2025-12-22T10:50:09"
  },
  ...
]
```

### 2. 타입별 설정 조회 (GET)
```bash
GET /cost-config/MEMBER_GRADE_CERTIFICATION
GET /cost-config/REVIEWER_GRADE_REVIEW
GET /cost-config/REFERRAL_GRADE_CHARGE_RATE
```

### 3. 설정 수정 (PUT) - 관리자만 가능 ⚠️
```bash
PUT /cost-config
Content-Type: application/json

{
  "configType": "MEMBER_GRADE_CERTIFICATION",
  "gradeName": "level1",
  "value": 2500000
}
```

**중요:** 이 요청은 자동으로 다음을 수행합니다:
- level1의 모든 InviteCost를 50만원(2500000 × 0.2)으로 업데이트
- 관련된 ChargeCost도 함께 재계산

## 🧪 테스트 시나리오

### 시나리오 1: 기업 규모별 인증 비용 수정
```bash
# level1의 인증 비용을 200만원 → 250만원으로 변경
curl -X PUT http://localhost:8080/cost-config \
  -H "Content-Type: application/json" \
  -H "Cookie: JSESSIONID=<관리자 세션 쿠키>" \
  -d '{
    "configType": "MEMBER_GRADE_CERTIFICATION",
    "gradeName": "level1",
    "value": 2500000
  }'

# 결과: level1의 모든 InviteCost가 50만원으로 재계산됨
```

### 시나리오 2: 심사원 등급별 심사비 수정
```bash
# 심사원보의 기본 심사비를 30만원 → 35만원으로 변경
curl -X PUT http://localhost:8080/cost-config \
  -H "Content-Type: application/json" \
  -H "Cookie: JSESSIONID=<관리자 세션 쿠키>" \
  -d '{
    "configType": "REVIEWER_GRADE_REVIEW",
    "gradeName": "심사원보",
    "value": 350000
  }'

# 결과: 심사원보의 모든 ReviewCost가 signcount를 반영하여 재계산됨
# 예: signcount가 2라면 70만원(35만원 × 2)으로 업데이트
```

### 시나리오 3: 추천 수수료 비율 수정
```bash
# 리더의 수수료 비율을 10% → 15%로 변경
curl -X PUT http://localhost:8080/cost-config \
  -H "Content-Type: application/json" \
  -H "Cookie: JSESSIONID=<관리자 세션 쿠키>" \
  -d '{
    "configType": "REFERRAL_GRADE_CHARGE_RATE",
    "gradeName": "리더",
    "value": 15
  }'

# 결과: 리더 등급 추천인의 모든 ChargeCost가 재계산됨
```

### 시나리오 4: 추천비 기본 금액 수정 ⭐ NEW!
```bash
# 추천비를 10만원 → 15만원으로 변경
curl -X PUT http://localhost:8080/cost-config \
  -H "Content-Type: application/json" \
  -H "Cookie: JSESSIONID=<관리자 세션 쿠키>" \
  -d '{
    "configType": "REFERRAL_COST_DEFAULT",
    "gradeName": "default",
    "value": 150000
  }'

# 결과: 모든 기존 ReferralCost가 15만원으로 재계산됨
# UserService에서 새로 가입하는 회원의 추천비도 15만원으로 자동 지급됨
```

## 🔐 인증 방법

API는 **관리자 권한**이 필요합니다. 테스트하려면:

1. 관리자 계정으로 로그인:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "loginID": "<관리자 ID>",
    "password": "<관리자 비밀번호>"
  }' \
  -c cookies.txt
```

2. 쿠키를 사용해서 API 호출:
```bash
curl -X GET http://localhost:8080/cost-config \
  -b cookies.txt
```

## 📊 초기 데이터

### MEMBER_GRADE_CERTIFICATION (기업 규모별 인증 비용)
| gradeName | value (인증 비용) | invitecost (영업비 = 인증 비용 × 0.2) |
|-----------|------------------|--------------------------------------|
| level1    | 2,000,000원      | 400,000원                            |
| level2    | 2,500,000원      | 500,000원                            |
| level3    | 3,500,000원      | 700,000원                            |
| level4    | 10,000,000원     | 2,000,000원                          |
| level5    | 20,000,000원     | 4,000,000원                          |

### REVIEWER_GRADE_REVIEW (심사원 등급별 기본 심사비)
| gradeName     | value (기본 심사비) |
|---------------|-------------------|
| 심사원보      | 300,000원         |
| 심사위원      | 400,000원         |
| 수석심사위원  | 500,000원         |

### REFERRAL_GRADE_CHARGE_RATE (추천 등급별 수수료 비율)
| gradeName | value (비율 %) |
|-----------|---------------|
| 리더      | 10%           |
| 일반      | 5%            |

### REFERRAL_COST_DEFAULT (추천비 기본 금액)
| gradeName | value (추천비) |
|-----------|---------------|
| default   | 100,000원     |

## ⚠️ 주의사항

1. **관리자만** 설정 수정 가능 (조회도 관리자만 가능하도록 설정됨)
2. 값 수정 시 **해당 등급의 모든 기존 데이터가 자동으로 재계산됨**
3. 재계산은 트랜잭션으로 처리되어 안전함
4. 수수료 비율은 **정수로 저장** (10 = 10%, 5 = 5%)

## ✅ 검증 방법

값 수정 후 재계산이 잘 되었는지 확인하려면:

1. 수정 전 Cost 조회:
```bash
GET /costs/invite/with-status  # InviteCost 조회
GET /costs/review/with-status  # ReviewCost 조회
GET /costs/charge/with-status  # ChargeCost 조회
```

2. 설정 값 수정

3. 수정 후 Cost 다시 조회하여 값이 변경되었는지 확인
