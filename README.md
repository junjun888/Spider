# Spider
这是一个基于 Java开发 爬取上海专利局专利元数据，和全文的爬虫。
采用了多线程技术。利用httpclie 模拟浏览器发起 GET/POST 请求, 利用 Jsoup 解析 html，利用mogodb去存储数据。
速度：10000+/小时。
