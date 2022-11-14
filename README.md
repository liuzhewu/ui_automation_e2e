# demo
支持一个系统的多个模块单个、端到端自动化测试。

## 环境
jdk11+testng+selenium+allure

## 设计说明
1.没有采用常用的po模式，页面元素的定位配置在ui.xml中，好处：1.可以复用页面元素（在常用定位中） 2.所有元素都在一个ui.xml中,查找方便 3.如果需要国际化需求，可以写对应的ui_language.xml解决，传统的po模式不好解决这个问题。
2.采用allure报告，美观、方便。
3.对元素的常见操作（sendKey、click、findElement）封装在com.liu.test.Base中，遇到大调整，可以只改底部，上面的业务逻辑代码无需变更。
4.断言都写在com.liu.asserts.Assertion中，切面编程，目前用于统一写日志。
5.加入自定义监听com.liu.listener.CustomListener，可以管理测试用例执行情况，目前主要是用于用例失败后做截图。


###项目说明：
支持多模块，所以存在菜单名重复的问题，需要加入模块名限制，test目录下core、cp是2个模块，e2e是端到端逻辑，会串联2个模块
由于实际业务原因，多模块的登录url是同一个的情况
一次只能执行一个模块的ui自动化，即执行一个testng文件

###执行方式：
1.选择需要的testng.xml，右键run,环境变量可通过common.properties的DEFAULT_ENV改变
2.命令：mvn test -Dsurefire.suiteXmlFiles=src/test/resources/testng/cp_testng.xml -Denv=ali

###模块说明：
在testng/*_testng.xml中的module中配置
<parameter name="module" value="CORE"/>
CP是配置平台Configure platform的模块简称
CORE是监控中心，原来叫配置平台mds-core

###涉及到多模块的内容，大部分都在resources目录下：
data.csv中的菜单列需要遵守最小菜单名
common.properties放置各环境共有的，最上面的放置各模块共有的。下面的放置各个模块独有，变量名前面要加上模块名，各个模块之间用注释分隔
ui.xml除常用定位、共有模块(比如登录)外， 其他page外面加上<page keyWord="模块名">，并且子page的keyword以模块名开头
Constant.java放置用户不需要改动和不关心的(只是程序需要的)变量，最上面的放置各模块共有的。下面的放置各个模块独有，变量名前面要加上模块名，各个模块之间用注释分隔
Tool.java 放置常用工具类，最上面的放置各模块共有的。下面的放置各个模块独有，变量名前面要加上模块名，各个模块之间用注释分隔
Base.java 放置测试类的常用操作，放置各模块共有的，如果有自己模块独特的方法，在test/模块下面新增一个 模块Base.java，如CmsBase，
模块中的测试类继承 模块Base.java。

###其他配置文件说明：
env/*.properties中放置各个环境(ali、wmmp)不同的变量
testng.xml根据需要配置各自的执行逻辑

###端到端测试的说明：
端到端的简称是e2e，单独的测试需求，重点在于，e2e可以复用其他模块的逻辑，并且csv中的
数据包括其他模块的数据，用于传参。比如：e2e调用A、B模块的逻辑，A、B模块对应需要的
csv中的数据为{name1:value;name2:value2},{name3:value;name2:value2},则
e2e的数据为{name1:value;name2:value2,name3:value}

###其他说明：
1.项目默认以无头(即不显示浏览器,常用在linux上)模式执行，可通过注释Before.java的System.setProperty("critic.headless", "True");改为有头模式
2.如果脚本因为升级后的开发bug导致不可用，注释，并补充gypt说明，类似：/*因系统bug导致注释 GYPT-44015 需要注释的代码 */
3.所有模块用同一个账号？缺点：可能存在冲突。如果每个模块一个账号，缺点：可能生产没有  
4.每个模块写代码的流程：自己的模块建立分支->调通->提交到master->jenkins取master分支跑，传的参数不一样  