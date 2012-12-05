介绍
=============

  Jvifm是一个用java写的, 有类似vi的键绑定的两列面板的文件管理器. 
此软件最初是仿linux控制台下的vifm写的, 操作基本类似. 因为是基于
GUI库SWT,所以比原vifm多了些功能, 并且能很好的在windows下运行.
 

常用操作:
=============

      空格 :在两个文件窗格之间切换
      j和k :光标上下移动
      h    :返回上级目录
      l    :如果当前条目是目录,则进入目录.如果是文件,则进行编辑

      gh   :转到home目录
      g/   :转到根目录

      dd   :剪切文件或目录
      yy   :复制文件或目录
      p    :粘贴文件或目录
      P    :粘贴系统剪切板上的文件

      m[a-z] :设置书签
      '[a-z] :转到书签的文件夹


其他操作可以在启动Jvifm用 :help　查看

截图
=============
![jie tu](https://raw.github.com/shrekwang/jvifm/master/site/jvifm.jpg)
