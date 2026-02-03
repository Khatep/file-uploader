import http from 'k6/http';
import { check, sleep } from 'k6';
import { open } from 'k6/fs';

export const options = {
    scenarios: {
        upload_250_rps: {
            executor: 'constant-arrival-rate',
            rate: 250,
            timeUnit: '1s',
            duration: '2m',
            preAllocatedVUs: 100,
            maxVUs: 500,
        },
    },
};

const BASE_URL = 'http://localhost:8080/api/v1/files/upload';
const testFile = open('./test-file.pdf', 'b');

export default function () {
    // Уникальный userId на каждый запрос
    const userId = (__VU * 1_000_000) + __ITER;

    const payload = {
        file: http.file(testFile, 'test-file.pdf', 'application/pdf'),
        userId: String(userId),
    };

    const res = http.post(BASE_URL, payload);

    check(res, {
        'status is 202': (r) => r.status === 202,
    });

    sleep(0.001);
}

//docker run --rm -i \
//   -v /Users/khatep/IdeaProjects/file-uploader/k6:/scripts \
//   --network host \
//   grafana/k6:latest run /scripts/upload_test.js