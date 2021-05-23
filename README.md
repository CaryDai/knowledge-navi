# 开放式专题知识库智能构建平台后端项目
## 文件介绍
application.properties：项目配置信息

controller文件夹：
* ClassCodesController：专利相关接口
* UserController：用户相关接口
dao文件夹、dataobject文件夹、resources/mapping文件夹：均属于数据层，除dataobject文件夹中部分文件外，其余都由mybatis-generator插件自动生成。
service文件夹：服务层方法
utils文件夹：工具类

## 技术架构
SpringBoot+Maven+MySQL+MyBatis

## 相关数据表
数据都存在实验室117服务器上，数据库为techpooldata。
本项目相关的数据表有：
* agriculture_tech、basic_science、engin_tech_one、engin_tech_two、information_tech、medicine_health（这几张表与节点关系图、树状图相关）
* patent表：存储所有专利数据
* patent_classId：存储节点分类号与专利的映射关系
* user_info：用户基本信息表，包括用户名、手机号，以及用户构建的专题库信息（分类号、专题库名、专题库描述信息，默认最多构建5个专题库）。
其中分类号的命名规则为：专题库序号+手机号后四位+用户从已有分类中选择的分类号。比如018337C041，01表示当前用户的第一个专题库，8337为手机号后四位，C041为选择的分类
* user_subjects：用户构建的分类信息（包括分类名、分类号和父分类号）
* user_patents：用户构建专题库时选择的专利数据，与相应的分类对应。
* user_password：用户密码表，注册登陆时被使用到

## 注意点
1. 少数方法没有被使用到，可以继续优化。
2. 查询专利的逻辑是：先查patent_classId表，再查patent表，因为patent_classId表中的数据比较新，所以有些公开号并没有对应的专利，需要多次查找
3. 不懂的地方多debug+问来源