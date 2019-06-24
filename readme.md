# 获取项目源码

```bash
git clone https://github.com/zhenye163/spider-zhihu
```

# 基础环境要求

`mysql5.6+`,`JDK8`,`Maven3.5.3+`

# 项目启动说明

1. 创建数据库`zhihu`

```sql
create database if not exists zhihu;
```

2. 编辑配置文件`application.properties`

```
# crawler配置
seimi.crawler.names=ipProxy-crawler
# 自定义属性配置
zhihu.root.name=excited-vczh
```

3. 启动项目

执行`mvn springboot:run`命令或在开发工具帮助下，启动项目。预先爬取并保存一些代理信息。

注：如果`ip_proxy`表中没有可用的代理，`Crawler`默认会采用本机IP去爬取数据。这样的话，我们的本机IP可能会被知乎封闭导致不可用。


4. 接口调用爬取知乎用户信息

在postman或浏览器地址栏中键入`localhost:8980/users/all`，发送HTTP请求调用相应的接口来获取并保存知乎用户信息。思路如下：

要爬取知乎用户信息的方式有很多种。这里采用的方式：预先选取一个知名大V---`excited-vczh`，爬取所有他关注和关注他的用户信息；然后再基于关注的关联关系，爬取其他用户的信息。简单来说，我们就是基于所有用户的关注关系形成的网状结构，采用`深度优先搜索`的思路爬取用户信息。这样是能够爬取到绝大部分用户的信息。（由于不需要完备的知乎用户数据，不在这个网状结构内的用户我们不关注，可以不爬取其数据）

5. 将数据导入elasticsearch中

待续...