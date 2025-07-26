
document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('naverRealtimeForm');
    const resultDiv = document.getElementById('naverRealtimeResult');
    const nSubmit = form.querySelector('button[type="submit"]');

    form.addEventListener('submit', function (e) {
        e.preventDefault();

        // 버튼 1초간 disabled
        nSubmit.disabled = true;
        setTimeout(() => { nSubmit.disabled = false; }, 1000);

        const rawInput = document.getElementById('symbol').value;

        if (rawInput.includes('<') || rawInput.includes('>')) {
            resultDiv.innerHTML = `
                <div class="error-box">
                  [ERROR]: 다음 입력은 사용할 수 없습니다.
                </div>
              `;
            nSubmit.disabled = false;
            return;
        }


        const marketValue = document.getElementById('marketSelect').value;
        const [region, exchange] = marketValue.split('_');
        const ticker = document.getElementById('symbol').value.trim().toUpperCase();

        if (!ticker) {
            alert("티커를 입력하세요.");
            return;
        }

        // Spring Boot 서버에 프록시 요청
        const proxyUrl = `/api/proxy/naver-stock/realtime?region=${region}&exchange=${exchange}&ticker=${ticker}`;

        resultDiv.classList.add('show');
        resultDiv.innerText = "🔄 실시간 시세 조회 중...";

        fetch(proxyUrl)
            .then(response => {
                if (response.status === 409) throw new Error("[ERROR] 거래소와 종목명을 다시 확인해 주세요");
                if (!response.ok) throw new Error("[ERROR] 기타 오류 발생");
                return response.json();
            })
            .then(data => {
                // NaN 상태로 그대로 포맷 출력 방지
                const closeRaw = data.closePrice ?? data.closePrice?.value;
                if (closeRaw === undefined || closeRaw === null || closeRaw === "") {
                    resultDiv.innerHTML = `
                        <div class="error-box">
                          오류: 거래소와 종목명을 다시 확인해 주세요
                        </div>
                      `;
                    return;
                }

                const tradedAt = new Date(data.localTradedAt);
                const formattedTime = tradedAt.toLocaleTimeString('ko-KR', {
                    hour:   '2-digit',
                    minute: '2-digit',
                    second: '2-digit'
                });

                const realTimeLabel = data.delayTimeName || '';

                const changeValue   = parseFloat(data.compareToPreviousClosePrice);
                const changePercent = parseFloat(data.fluctuationsRatio);
                let arrow = '', arrowClass = '';
                if (changeValue > 0) {
                    arrow      = '▲';
                    arrowClass = 'up';
                } else if (changeValue < 0) {
                    arrow      = '▼';
                    arrowClass = 'down';
                }

                const close = data.closePrice || data.closePrice?.value;
                const stockName = data.stockName || ticker;
                const market = data.stockExchangeName || '해외';

                resultDiv.innerHTML = `
                  <div class="result-card">
                    <div class="timestamp">${formattedTime} ${realTimeLabel}</div>
                    <h3>${stockName} (${ticker})</h3>
                    <p><strong>거래소:</strong> ${market}</p>
                    <p><strong>현재가:</strong> ${close}</p>
                    <p class="change-line">
                      <strong>전일 대비:</strong>
                      <span class="arrow ${arrowClass}">${arrow}</span>
                      ${Math.abs(changeValue).toFixed(2)} (${changePercent.toFixed(2)}%)
                    </p>
                  </div>
                `;
            })
            .catch(err => {
                console.error(err);
                resultDiv.innerHTML = `<div class="error-box">${err}</div>`;
            });
    });

});