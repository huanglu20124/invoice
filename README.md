## OverView

智能发票识别系统

## Requirements

* tomcat服务器
* eclipse
* mysql数据库
* redis数据库

## Function

* 自动归类识别机打发票中的发票信息
* 用户手动绘制用于识别的发票模板和识别区域
* 查看等待识别发票的任务缓冲队列
* 可视化发票识别算法的过程，动态展示当前识别的区域和结果
* 管理员可对系统平台内的用户、用户组进行权限编辑管理
* 单位负责人可修改使用系统平台的单位信息
* 个人设置可查看个人信息和权限

## Details

### 前端

* Jquery + bootstrap搭建前端框架，处理前端逻辑和展示，负责MVC架构中的View视图层
* 前后端通过websocket和ajax通信，ajax主要用于按钮等控件的事件处理函数中的请求，websocket用于后端主动向前端推送消息
* JSP控制cookies和session，在页面跳转时记录会话用户态，并可通过前端可视化界面对用户权限（用户权限分为继承的用户组权限和个人权限）进行编辑
* 识别算法的可视化通过websocket实现，算法端将每个区域的识别结果通过后台服务器逐次转送给前端，前端在`onmessage`回调函数中处理信息并在可视化窗口的canvas画布中显示出来
* 通过画布的`getImageData`和`putImageData`获取图片的像素点，并制造模糊效果，对比突出当前的识别区域
* Js + canvas实现用户动态画图的效果，可以在canvas画布中框出自定义的识别区域和填写区域信息

### 后端

* Spring MVC + Spring + JDBC搭建后端框架，Controller负责接收请求，Service负责主要业务逻辑，Dao负责数据库访问
* 算法端用C++编写，java后端通过多线程+socket+TCP与算法端通讯，利用对象锁完成线程调度
* 利用redis实现缓冲队列以及模板操作调度队列
* 利用spring-websocket与前端进行全双工通信
* 使用shiro作为安全管理框架，通过其内置session实现安全登录，使用shiro注解完成权限管理。



## Screenshot

* 缓冲队列<br/>
![img](https://github.com/huanglu20124/ImgRespository/blob/master/invoice/缓冲队列.png?raw=true)

* 算法可视<br/>
![img](https://github.com/huanglu20124/ImgRespository/blob/master/invoice/算法可视.png?raw=true)

* 发票模板<br/>
![img](https://github.com/huanglu20124/ImgRespository/blob/master/invoice/发票模板1.png?raw=true)

* 添加/修改模板<br/>
![img](https://github.com/huanglu20124/ImgRespository/blob/master/invoice/模板2.png?raw=true)

* 未识发票<br/>
![img](https://github.com/huanglu20124/ImgRespository/blob/master/invoice/未识别发票.png?raw=true)

* 日志查询<br/>
![img](https://github.com/huanglu20124/ImgRespository/blob/master/invoice/日志查询.png?raw=true)

* 用户管理<br/>
![img](https://github.com/huanglu20124/ImgRespository/blob/master/invoice/用户管理.png?raw=true)

* 单位管理<br/>
![img](https://github.com/huanglu20124/ImgRespository/blob/master/invoice/单位管理.png?raw=true)

* 个人设置<br/>
![img](https://github.com/huanglu20124/ImgRespository/blob/master/invoice/个人设置.png?raw=true)

## Author

| Author | E-mail |
| :------:  | :------: |
| Eric_Wong |  564945308@qq.com |
| huanglu | 845758437@qq.com |
