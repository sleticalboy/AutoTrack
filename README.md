# AutoTrack

## 代理 View.OnClickListener

## 代理 Window.Callback

## 代理 View.AccessibilityDelegate

## 透明层

## AspectJ

- [51 信用卡 Android 自动埋点实践](https://www.infoq.cn/article/dhfXLVGa7e9kEX_pbtoZ)

### 自定义 gradle 插件

- 目录结构

```shell
xxx/src
└── main
    ├── groovy // 这里是源码目录
    │   └── // 具体的包结构
    └── resources // 这里是配置文件目录
        └── META-INF // 名字一定不能错误，一个字符都不能错
            └── gradle-plugins // 名字一定不能错误，一个字符都不能错
                └── plugin_name.properties // apply plugin: 'plugin_name' 这两个地方是保持一致的
// 关于 xxx.properties 内容
implementation-class=插件类的全路径 (不要有任何多余的字符!!)
```

- 每次更改完插件，如果用的是 local repo，需要把引用的地方先注释掉，然后重新上传，

## ASM

### 自定义 Transform

- com.android.build.api.transform.TransformException: java.util.zip.ZipException: error in opening
  zip file
    - stream 忘记 close 了

## Javassist

## AST
