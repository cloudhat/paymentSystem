<h1>이커머스 결제 서비스를 구현한 토이프로젝트</h1>
<p><a href="https://cloudhat.github.io/posts/payment-system/">상세설명 링크</a></p>
    <h2>아래 6단계로 이루어진 이커머스 결제 서비스를 구현한 토이프로젝트입니다.</h2>
    <ol>
        <li>회원 가입 및 로그인</li>
        <li>장바구니 생성</li>
        <li>주문 옵션 선택(결제수단, 배송지, 할인수단 등)</li>
        <li>결제</li>
        <li>주문내역 조회</li>
        <li>주문 취소</li>
    </ol>
        <h2 id="e23a1be8-8347-4c05-9565-3d12c01b2a4e" class=""><strong>토이프로젝트의 기술적 목표는 아래와 같습니다.</strong></h2>
        <ul id="e14d1d3c-fb1e-4690-978d-b91a5af9d8de" class="bulleted-list">
            <li style="list-style-type:disc">1)새로운 비즈니스 요구에 맞게 유연하게 확장 가능하도록 설계</li>
        </ul>
        <ul id="e6756108-4605-4e05-84b8-d41f0d640a9d" class="bulleted-list">
            <li style="list-style-type:disc">2)장애상황에 안정적으로 대응 가능하도록 설계</li>
        </ul>
        <p id="07348a80-7e99-4b7b-afa4-fe8d0cfdd413" class="">
        </p>
        <h2 id="e8083913-9788-46b8-a370-972e254db2e2" class=""><strong>위 목표를 달성하기 위해 아래 3가지 기준을 중심으로 구현했습니다.</strong>
        </h2>
        <p id="0e716f93-76f1-42e8-98e8-ea7f2fa46020" class="">
        </p>
        <p id="53e37fa2-650a-4e78-a876-f2866d2cbdba" class=""><strong>1)예외처리</strong></p>
        <ul id="fc49a4d6-f97f-451d-b5b3-44f1e429327e" class="bulleted-list">
            <li style="list-style-type:disc">장애는 언제든지 다양한 요인으로 발생할 수 있습니다.</li>
        </ul>
        <ul id="503debb0-d861-4094-8a0a-1b0fce41677e" class="bulleted-list">
            <li style="list-style-type:disc">특히 주문-결제 서비스의 경우 다양한 계층에서 예외 케이스가 발생할 수 있으며 이는 사용자 경험에 큰 악영향으로 작용합니다.</li>
        </ul>
        <ul id="5334e9a1-50e6-4f65-96f0-c7c341363065" class="bulleted-list">
            <li style="list-style-type:disc">이에 따라 가능한 사용자 경험에 악영향이 없도록 예외처리를 구현했습니다.</li>
        </ul>
        <p id="0ebb4626-c10e-49d5-82bf-09e6edd5a3a2" class="">
        </p>
        <p id="a8f22375-9576-46d3-8561-ec7eea65f422" class=""><strong>2)객체지향</strong></p>
        <ul id="cb7a7a65-0861-4af0-9a5d-10677edc137a" class="bulleted-list">
            <li style="list-style-type:disc">객체지향이 항상 최고의 방법론은 아닙니다.</li>
        </ul>
        <ul id="52ad7895-1cbe-4bff-90c0-bcc219e6bc94" class="bulleted-list">
            <li style="list-style-type:disc">하지만 아래 2가지의 장점을 고려하여 이번 토이프로젝트에서는 객체지향 프로그래밍을 적극적으로 추구했습니다.
                <ul id="b5b3044e-3855-409b-9410-7a6bbf64403f" class="bulleted-list">
                    <li style="list-style-type:circle">1)안정적으로 변화에 대응할 수 있습니다.
                        <ul id="98067346-29a1-40d4-8815-114b91e6ef5f" class="bulleted-list">
                            <li style="list-style-type:square">주문 및 결제기능은 고객의 지갑과 직접적으로 연관되어 있어 특히 안정성이 요구됩니다.</li>
                        </ul>
                        <ul id="7ed11e30-94fe-4d21-97d1-0f3a26af75b2" class="bulleted-list">
                            <li style="list-style-type:square">객체지향 프로그래밍은 객체에게 역할을 적절하게 분배합니다. 덕분에 기존의 코드를 가능한 적게
                                수정하면서도 새로운 기능을 안정적으로 추가할 수 있습니다.
                            </li>
                        </ul>
                    </li>
                </ul>
                <ul id="65ba6516-c532-4ed2-8bf4-814af5c53935" class="bulleted-list">
                    <li style="list-style-type:circle">2)이번 토이프로젝트에서 선택한 기술 Spring - JPA에 가장 적합한 패러다임입니다.</li>
                </ul>
            </li>
        </ul>
        <p id="f545bf2b-6ce5-4343-b530-05bd20d41275" class="">
        </p>
        <p id="810e8bb0-64ad-4500-8bde-8447b3b7f6e0" class=""><strong>3)시나리오 기반 E2E 테스트 코드 작성</strong></p>
        <ul id="495cda28-179c-4be6-b876-e2c01a4aeb20" class="bulleted-list">
            <li style="list-style-type:disc">실제 주문 및 결제 서비스 호출 시 발생하는 프로세스와 동일하도록 시나리오에 따라 E2E 테스트로 코드를 검증하도록 구현했습니다.
                <ul id="afc45077-a025-4691-a681-950d595fd3d3" class="bulleted-list">
                    <li style="list-style-type:circle">이는 각기 분리되어 있는 주문 및 결제 프로세스가 Production 환경에서도 안정적으로 작동하도록 신뢰성을 높이기
                        위함입니다.
                    </li>
                </ul>
            </li>
        </ul>
        <ul id="c1819278-a176-49b9-bc0a-9f248b833897" class="bulleted-list">
            <li style="list-style-type:disc">복잡한 로직의 경우 unit 테스트로 보완하였습니다.</li>
        </ul>
    
