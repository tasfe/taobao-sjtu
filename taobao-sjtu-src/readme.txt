要启用java的assertion功能，需要在jvm的编译参数中开启，添加-ea参数来达到这一目的；

  在JDK1.4中增加了Assert的新功能，用于开始、调试时期的错误检测，它可以通过-ea 的JVM参数关闭，关闭以后对程序的性能没有任何影响。

     最好的教程 请见：http://java.sun.com/j2se/1.4.2/docs/guide/lang/assert.html

     简单总结下几点注意点

不要在public方法中使用assert来代替参数检查。公有方法的参数检查应该有该方法通过条件判断等方式完成，从而抛出理想的异常，而其只会抛出：AssertionError，对异常并不是很明确。AssertionError 继承自Error。
它的语法有两种：
assert Expression1

 ：

Expression1

 为条件判断，当其为true时程序正常，为false时，程序抛出AssertError。

  assert Expression1

 : Expression2

 ：Expression1
同上，

Expression2必须是有返回值的函数或者其它字符串、变量等。
       3. 编译： javac -source 1.4 MyClass.java

            启动： java -ea MyClass

            Eclipse的话，需要在Run=>JVM Argements 中增加-ea参数