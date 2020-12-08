### 注意:请将lib_common下manifest中喜马拉雅key替换为自己的,不然会提示访问超过限制.
### 本项目仅提供学习参考,不可作为商用.

### 新增传统AMS页面管理,参考develop_ams分支

### 更新日志：

###### 珠穆朗玛FM 1.0 2020-12.7

* 通过后台动态配置首页tab栏

###### 珠穆朗玛FM 1.0 2020-11.19

* 新增传统ams页面管理,参考develop_ams分支

###### 珠穆朗玛FM 1.0 2019-11.22
* 适配AndroidX
###### 珠穆朗玛FM 1.0 2019-10.31
* 使用BackgroundLibrary库代替所有shape资源,请无视xml文件中app:bl_xxxx_xxxx="xxxx"错误
* 引入databinding,减少findViewById使用
###### 珠穆朗玛FM 1.0 2019-10.22
* 用户登陆
###### 珠穆朗玛FM 1.0 2019-10.09
* 友盟分享
###### 珠穆朗玛FM 1.0 2019-09.25
* 布局优化,避免过渡绘制
* 完善状态管理,提升用户体验
###### 珠穆朗玛FM 1.0 2019-09.20
* 声音批量下载
* 语音搜索
* 专辑订阅
* 声音喜欢
* 优化启动速度
* 加入模拟广告页
* 集成Bugly异常上报,全量更新,热更新
###### 珠穆朗玛FM 1.0 2019-09.13
* 第一次发布

### 功能演示
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201211185112677.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MjcwMzQ0NQ==,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191021163929286.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MjcwMzQ0NQ==,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191021164006231.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MjcwMzQ0NQ==,size_16,color_FFFFFF,t_70)

### 主要功能(包括不仅限于):
* 专辑展示
* 声音展示及播放
* 电台展示及播放
* 主播展示
* 声音下载
* 历史播放展示及播放
### 待完善(包括不仅限于):
* 优化UI
* 优化性能
* 修复bug

### 快速体验

扫描二维码下载：
![](screenshot/download.png)

如果二维码图片不可见，[点我下载体验](https://www.pgyer.com/lxEs)密码1111
### 项目架构
本项目采用retrofit+rxjava2+rxandroid+arouter+mvvm+fragmentation实现单activity多fragme组件化架构

* app：负责管理各个业务组件，和打包apk，没有具体的业务功能；
* lib_third：负责第三方库的集成和初始化;
* lib_common：属于功能组件，支撑业务组件的基础，提供多数业务组件需要的功能;
* module_main：属于业务组件，指定APP启动页面、主界面；
* module_home：首页展示,包括热门,分类,精品,主播,电台,展示及播放等；
* module_listen：我听,包括订阅,喜欢,播放历史,下载等；
* module_discover：发现,包括后续扩展功能等；
* module_user：用户管理模块;


### 组件化实现：

珠穆朗玛FM客户端使用阿里ARouter作为路由，实现组件与组件的通信跳转

### 集成模式和组件模式转换
Module的属性是在每个组件的 build.gradle 文件中配置的，当我们在组件模式开发时，业务组件应处于application属性，这时的业务组件就是一个 Android App，可以独立开发和调试；而当我们转换到集成模式开发时，业务组件应该处于 library 属性，这样才能被我们的“app壳工程”所依赖，组成一个具有完整功能的APP

先打开工程的根目录下找到gradle.properties 文件，然后将 isModule 改为你需要的开发模式（true/false）， 然后点击 "Sync Project" 按钮同步项目
```
isModule=false
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190530142030271.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9tZW54aW5kaWFvbG9uZy5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)
```
if (isModule.toBoolean()) {
    apply plugin: 'com.android.application'
} else {
    apply plugin: 'com.android.library'
}
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190530142043496.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9tZW54aW5kaWFvbG9uZy5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)
### 组件之间AndroidManifest合并问题
我们可以为组件开发模式下的业务组件再创建一个 AndroidManifest.xml，然后根据isModule指定AndroidManifest.xml的文件路径，让业务组件在集成模式和组件模式下使用不同的AndroidManifest.xml，这样表单冲突的问题就可以规避了
已module_main组件为例配置如下：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190530150350275.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9tZW54aW5kaWFvbG9uZy5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)

```
sourceSets {
        main {
            if (isModule.toBoolean()) {
                manifest.srcFile 'src/main/module/AndroidManifest.xml'
            } else {
                manifest.srcFile 'src/main/AndroidManifest.xml'
            }
        }
}
```
### 组件模式下的Application
在每个组件的debug目录下创建一个Application并在module下的AndroidManifest.xml进行配置
配图：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190530142154452.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9tZW54aW5kaWFvbG9uZy5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)
### 集成开发模式下的Application
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190530150933283.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9tZW54aW5kaWFvbG9uZy5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)
### 主要用到的开源库
* 快速切面编程开源库 https://github.com/TanZhiL/OkAspectj
* 喜马拉雅SDK http://open.ximalaya.com/
* fragmentation https://github.com/YoKeyword/Fragmentation
* okhttp https://github.com/square/okhttp 
* retrofit https://github.com/square/retrofit
* rxpermissions 权限 https://github.com/tbruyelle/RxPermissions
* BaseRecyclerViewAdapterHelper https://github.com/CymChad/BaseRecyclerViewAdapterHelper
* loadsir 状态管理 https://github.com/KingJA/LoadSir
* lottie动画 https://github.com/airbnb/lottie-android
* SmartRefreshLayout https://github.com/scwang90/SmartRefreshLayout
* MagicIndicator https://github.com/hackware1993/MagicIndicator
* shape神器BackgroundLibrary https://github.com/JavaNoober/BackgroundLibrary
### 致谢
* 感谢所有开源库的大佬
* mvvm借鉴 https://github.com/geduo83/FlyTour 
* 原型平台 https://www.xiaopiu.com/
### 问题反馈
欢迎加星，打call https://github.com/TanZhiL/Zhumulangma
* email：1071931588@qq.com
### 关于作者
谭志龙
### 开源项目
* 快速切面编程开源库 https://github.com/TanZhiL/OkAspectj
* 高仿喜马拉雅听Android客户端 https://github.com/TanZhiL/Zhumulangma
* 骨架屏弹性块 https://github.com/TanZhiL/SkeletonBlock
* RxPersistence是基于面向对象设计的快速持久化框架 https://github.com/TanZhiL/RxPersistence
* greenDao Converter自动生成器 https://github.com/TanZhiL/GreenConverter
### License
```
Copyright (C)  tanzhilong Zhumualangma Framework Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
