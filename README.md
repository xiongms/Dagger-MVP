# Dagger-MVP

使用MVP、Dagger2.android、Rxjava2、Arouter快速搭建组件化应用架构，重点关注模块化、代码隔离、按需编译等问题

## 划重点
- 使用MVP多关注代码结构、整体架构、可测试性、可维护性这四个方面
- 通过Dagger2.android自动注入Presenter对象，减少大量的模板代码
- 自动完成View层和Presenter层的绑定与解绑
- 集成bugly的异常上报、热修复，基于微信tinker开源项目
- 全局注入Error、Empty、Loading等场景
- 使用Retrofit-url-manager动态管理接口baseUrl
- 通用Proguard混淆脚本

![image.png](https://raw.githubusercontent.com/xiongms/Dagger-MVP/master/%E6%9E%B6%E6%9E%84%E5%9B%BE.jpg)
