# XDanmuku
一种支持多种弹幕样式的弹幕视图控件

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
	        compile 'com.github.hust201010701:XDanmuku:33a063b46e'
	}

其他添加依赖的方式，如maven等请自行到[点我](https://jitpack.io/#hust201010701/XDanmuku/33a063b46e)查看。

## 1. 添加控件

在布局xml中添加控件

	<com.orzangleli.xdanmuku.DanmuContainerView
        android:id="@+id/danmuContainerView"
        android:layout_width="match_parent"
        android:layout_height="240dp"
        />

## 2. 自定义弹幕实体类DanmuEntity

根据需求定制弹幕实体类，包含所有弹幕的属性和类型。

	public class DanmuEntity {
	    public String content;
	    public int textColor;
	    public int backgroundColor;
	    public int type;
	    public String time;
	}

## 3. 添加DanmuConverter（弹幕转换器）

`DanmuConverter`中有两个抽象方法需要实现，`getSingleLineHeight`是返回所有弹幕样式中高度最大值作为弹幕航道的高度；`convert`负责将弹幕实体类`DanmuEntity`绑定到弹幕子视图上（类似于`BaseAdapter`的`getView`方法的作用）。

	DanmuConverter danmuConverter = new DanmuConverter<DanmuEntity>() {
        @Override
        public int getSingleLineHeight() {
            //将所有类型弹幕的布局拿出来，找到高度最大值，作为弹道高度
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_danmu, null);
            //指定行高
            view.measure(0, 0);

            View view2 = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_super_danmu, null);
            //指定行高
            view2.measure(0, 0);

            return Math.max(view.getMeasuredHeight(),view2.getMeasuredHeight());
        }

        @Override
        public View convert(DanmuEntity model) {
            View view = null;
            if(model.getType() == 0) {
                view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_danmu, null);
                TextView content = (TextView) view.findViewById(R.id.content);
                ImageView image = (ImageView) view.findViewById(R.id.image);
                image.setImageResource(ICON_RESOURCES[random.nextInt(5)]);
                content.setText(model.content);
                content.setTextColor(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            }
            else if(model.getType() == 1) {
                view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_super_danmu, null);
                TextView content = (TextView) view.findViewById(R.id.content);
                content.setText(model.content);
                TextView time = (TextView) view.findViewById(R.id.time);
                time.setText(model.getTime());
            }
            return view;
        }
    };

## 4. 添加弹幕

	DanmuEntity danmuEntity = new DanmuEntity();
    danmuEntity.setContent(doubleSeed.substring(index, index + 2 + random.nextInt(20)));
    danmuEntity.setType(random.nextInt(2));
    danmuEntity.setTime("23:20:11");
    try {
        danmuContainerView.addDanmu(danmuEntity);
    } catch (Exception e) {
        e.printStackTrace();
    }

## 5. 弹幕点击事件监听

	//弹幕点击事件
    danmuContainerView.setOnItemClickListener(new DanmuContainerView.OnItemClickListener<DanmuEntity>() {
        @Override
        public void onItemClick(DanmuEntity danmuEntity) {
            Toast.makeText(MainActivity.this,danmuEntity.content,Toast.LENGTH_SHORT).show();
        }
    });

## 6. 设置弹幕移动速度

`DanmuContainerView`中预设了三种弹幕移动速度：

	public final static int LOW_SPEED = 1;
    public final static int NORMAL_SPEED = 3;
    public final static int HIGH_SPEED = 5;

设置速度通过`setSpeed`方法：

	danmuContainerView.setSpeed(DanmuContainerView.HIGH_SPEED);

同时你可以传递具体的整数型速度：

	danmuContainerView.setSpeed(4);

## 7. 弹幕显示区域

本人将弹幕控件按照竖向均分为3份，分别为`GRAVITY_TOP`,`GRAVITY_CENTER`,`GRAVITY_BOTTOM`。用户可以自由组合显示区域，默认情况下全区域（`GRAVITY_FULL`）显示。设置要显示的区域通过`setGravity`方法实现，参数可以使用 **`|`** 进行连接。

	//只在上方和中间区域显示弹幕
	danmuContainerView.setGravity(DanmuContainerView.GRAVITY_TOP | DanmuContainerView.GRAVITY_CENTER);


# 后记

本控件的原理你可能已经知道了使用自定义ViewGroup实现。但是之前我花了很多事件尝试通过自定义LayoutManager让RecyclerView实现弹幕控件，不过最终这种方案失败了，更多细节讨论欢迎发送邮件(orzangleli@163.com)给我。

欢迎Star,提交Issues。

