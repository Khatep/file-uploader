import http from 'k6/http';
import { check, sleep } from 'k6';

/* ===== init stage ===== */
const file = open('./test-test.pdf', 'b');

export const options = {
    scenarios: {
        upload_100_requests: {
            executor: 'shared-iterations',
            vus: 10,
            iterations: 100,
            maxDuration: '2m',
        },
    },
    thresholds: {
        http_req_failed: ['rate<0.01'],
        http_req_duration: ['p(95)<5000'],
    },
};

export default function () {
    // Генерируем уникальный userId от 1 до 100
    const userId = (__VU - 1) * 10 + __ITER + 1;

    const payload = {
        file: http.file(file, 'test-test.pdf', 'application/pdf'),
        userId: userId,
    };

    const res = http.post(
        'http://localhost:8080/api/v1/files/upload',
        payload
    );

    check(res, {
        'status is 202': (r) => r.status === 202,
    });

    sleep(1);
}