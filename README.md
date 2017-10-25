# XDanmuku V1.1版本

[![](https://jitpack.io/v/hust201010701/XDanmuku.svg)](https://jitpack.io/#hust201010701/XDanmuku)
![](https://img.shields.io/badge/Android-View-brightgreen.svg)
[![license](https://img.shields.io/github/license/mashape/apistatus.svg)]()

## 更新内容 V1.1
1. 移动View线程数修改为1
2. 加入View缓存，并能自动调整缓存空间大小
3. 修改Entity绑定View的方式

## 使用方法

# XDanmuku
一种支持多种弹幕样式的弹幕视图控件(点击可以查看添加库的方法)。

[![](https://jitpack.io/v/hust201010701/XDanmuku.svg)](https://jitpack.io/#hust201010701/XDanmuku)

本项目是一个开源的弹幕控件库，能够支持多种样式弹幕，弹幕点击监听，弹幕分区域显示，自定义移动速度等功能，项目原理是通过自定义ViewGroup。可能是目前轻量级弹幕库中功能最强大的一款了。


# 效果
- 常规样式

![](http://7bvaky.com2.z0.glb.qiniucdn.com/2017-04-13_10_56_40_QQ图片20170413105220.png?imageView/2/w/500/)

- 点击事件

![](http://7bvaky.com2.z0.glb.qiniucdn.com/2017-04-13_10_56_40_QQ图片20170413105247.png?imageView/2/w/500/)

- 多种弹幕样式

![](http://7bvaky.com2.z0.glb.qiniucdn.com/2017-04-13_10_57_17_duoyangshi.png?imageView/2/w/500/)

- 分区域显示

![](http://7bvaky.com2.z0.glb.qiniucdn.com/2017-04-13_10_57_17_QQ图片20170413105441.png?imageView/2/w/500/)

- GIF效果图

![](http://7bvaky.com2.z0.glb.qiniucdn.com/2017-04-13_10_57_17_anim.gif)

# 使用

## 0. 添加依赖

**1. 导入`xdanmuku`源码**

你可以直接下载本项目`xdanmuku`模块，并导入项目目录，并添加依赖`compile project(':xdanmuku')`

**2. Gradle**

[![](https://jitpack.io/v/hust201010701/XDanmuku.svg)](https://jitpack.io/#hust201010701/XDanmuku)

先把jitpack仓库添加到项目根 `build.gradle（Project）`文件中，

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

然后在你的项目中添加依赖

	dependencies {
	        compile 'com.github.hust201010701:XDanmuku:-SNAPSHOT'
	}

其他添加依赖的方式，如maven等请自行到[点我](https://jitpack.io/#hust201010701/XDanmuku/-SNAPSHOT)查看。

## 1. 添加控件

在布局xml中添加控件

	<com.orzangleli.xdanmuku.DanmuContainerView
        android:id="@+id/danmuContainerView"
        android:layout_width="match_parent"
        android:layout_height="240dp"
        />

## 2. 添加自定义弹幕Entity（需要继承自Model）

类似于[DanmuEntity.java](https://github.com/hust201010701/XDanmuku/blob/master/app/src/main/java/com/orzangleli/danmudemo/DanmuEntity.java)



## 3. 继承XAdapter

类似于ListView的BaseAdapter的结构，具体参照 [DanmuAdapter.java](https://github.com/hust201010701/XDanmuku/blob/master/app/src/main/java/com/orzangleli/danmudemo/DanmuAdapter.java)


## 4. 添加弹幕

    DanmuEntity danmuEntity = new DanmuEntity();
    danmuEntity.setContent(SEED[random.nextInt(5)]);
    danmuEntity.setType(0);
    danmuEntity.setTime("23:20:11");
    danmuContainerView.addDanmu(danmuEntity);

## 5. 弹幕点击事件监听

    //弹幕点击事件
    danmuContainerView.setOnItemClickListener(new DanmuContainerView.OnItemClickListener() {
        @Override
        public void onItemClick(Model model) {
            DanmuEntity danmuEntity = (DanmuEntity) model;
            Toast.makeText(MainActivity.this, danmuEntity.content, Toast.LENGTH_SHORT).show();
        }
    });

## 6. 设置弹幕移动速度

`DanmuContainerView`中预设了三种弹幕移动速度：

    public final static int LOW_SPEED = 1;
    public final static int NORMAL_SPEED = 4;
    public final static int HIGH_SPEED = 8;

设置速度通过`setSpeed`方法：

	danmuContainerView.setSpeed(DanmuContainerView.HIGH_SPEED);

同时你可以传递具体的`int`型速度(建议速度值在1-8之间，数值越大速度越快)：

	danmuContainerView.setSpeed(5);

## 7. 弹幕显示区域

本人将弹幕控件按照竖向均分为3份，分别为`GRAVITY_TOP`,`GRAVITY_CENTER`,`GRAVITY_BOTTOM`。用户可以自由组合显示区域，默认情况下全区域（`GRAVITY_FULL`）显示。设置要显示的区域通过`setGravity`方法实现，参数可以使用 **`|`** 进行连接。

	//只在上方和中间区域显示弹幕
	danmuContainerView.setGravity(DanmuContainerView.GRAVITY_TOP | DanmuContainerView.GRAVITY_CENTER);


## 致谢

感谢以下用户的建议和反馈：

- [tz-xiaomage](https://github.com/tz-xiaomage)
- [kaient](https://juejin.im/user/57ed378da22b9d005bae9811)
- [amszsthl](https://github.com/amszsthl)
- [gaochunchun](https://github.com/gaochunchun)
- [narakai](https://github.com/narakai) 提出使用SparseArray代替HashMap维护缓存池。

## 附录

有几篇开发本库时的记录和心得，欢迎大家阅读点赞：

- **可能是目前轻量级弹幕控件中功能最强大的一款**
	- [orzangleli's blog](http://www.orzangleli.com/2017/04/14/2017-04-14_%E4%B8%80%E7%A7%8D%E6%94%AF%E6%8C%81%E5%A4%9A%E7%A7%8D%E5%BC%B9%E5%B9%95%E6%A0%B7%E5%BC%8F%E7%9A%84%E5%BC%B9%E5%B9%95%E8%A7%86%E5%9B%BE%E6%8E%A7%E4%BB%B6/)
	- [掘金](https://juejin.im/post/58eeed368d6d81006465670f)
- **基于XDanmuku的Android性能优化实战**
	- [orzangleli's blog](http://www.orzangleli.com/2017/04/17/2017-04-17_%E5%9F%BA%E4%BA%8EXDanmuku%E7%9A%84Android%E6%80%A7%E8%83%BD%E4%BC%98%E5%8C%96%E5%AE%9E%E6%88%98/)
	- [掘金](https://juejin.im/post/58f4de53da2f60005d3fe0e7)



## MIT License

Copyright (c) 2017 orzangleli

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

