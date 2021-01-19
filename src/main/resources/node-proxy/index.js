const httpProxy = require('http-proxy');
const express = require('express');

const app = express();
const proxy = httpProxy.createProxyServer({
    cookieDomainRewrite: "topikhanoi.com"
});

app.use((req, res) => {
    req.headers.host = 'topikhanoi.com';
    console.log(req.headers.cookie);
    proxy.web(req, res, { target: 'http://topikhanoi.com' });
});

app.listen(1248);