import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    scenarios: {
        spike: {
            executor: 'ramping-arrival-rate',
            startRate: 10,
            timeUnit: '1s',
            preAllocatedVUs: 50,
            maxVUs: 500,
            stages: [
                { duration: '30s', target: 200 },  // 快速冲击
                { duration: '1m',  target: 400 },  // 峰值
                { duration: '1m',  target: 50 },   // 回落
            ],
        },
    },
    thresholds: {
        'http_req_failed': ['rate<0.1'], // 突发允许更高失败率
    },
};

const BASE_URL = __ENV.BASE_URL || 'http://host.docker.internal:30080';
// 这个接口请指向一个“容易触发下游慢/失败”的路径，或网关的“故障注入”开关
const PATH = __ENV.SPIKE_PATH || '/api/train/search?from=BJ&to=GZ&date=2025-09-10';

export default function () {
    const res = http.get(`${BASE_URL}${PATH}`, { tags: { endpoint: 'spike' } });

    // 关键：当降级生效时，可能会返回“fallback”的特定响应体/标记（按你的实现改这里）
    check(res, {
        'status is 200/204/503': r => [200,204,503].includes(r.status),
        'fallback keyword (optional)': r => r.body && r.body.includes('fallback'),
    });

    sleep(0.3);
}
