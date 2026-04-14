import http from 'k6/http';

export default function () {
    http.get('http://localhost:8080/api/test', {
        headers: {
            'x-api-key': 'YOUR_API_KEY',
        },
    });
}