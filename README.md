# AutoTrack

## 代理 View.OnClickListener

## 代理 Window.Callback

## 代理 View.AccessibilityDelegate

## 透明层

## AspectJ

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

## ASM

## Javassist

## AST
