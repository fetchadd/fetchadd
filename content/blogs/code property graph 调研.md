+++
title = "cpg analysis"
date = 2019-07-26
draft = true
+++

### code property graph 调研

#### 1. 资料

https://joern.io/docs/essentials/installing/

https://docs.shiftleft.io/shiftleft/using-shiftleft-ocular/getting-started 

ocular是shiftleft的java2cpg产品，比开源的joern稍微好用点，功能更齐全，有14天的试用期，只能做一些基本的分析（无规则），正式版有。

实际项目中(fastjson) callgraph无法使用，所以使用ocular，但好像也有点问题

https://research.cs.wisc.edu/wpis/papers/popl95.pdf



#### 2. 结构

##### 2.1 AST

应该是由antr4生成的ast



AST2CPG:

| AST                           | CPG                                                | AST visit                   |
| :---------------------------- | :------------------------------------------------- | --------------------------- |
| Paramter                      | METHOD_PARAMETER_IN                                |                             |
| Argument                      |                                                    | Expression                  |
| ArgumentList                  |                                                    | Argument                    |
| AssignmentExpression          |                                                    | BinaryExpr                  |
| BinaryExpr                    | CALL&METHOD_INST                                   |                             |
| ClassDefStatement             | TYPE_DECL                                          | content                     |
| Statement                     | not imp                                            |                             |
| CastTarget                    | UNKNOW                                             |                             |
| InitializeList                | not imp                                            |                             |
| PtrMemberAccess               | CALL(indirectMemberAccess)&METHOD_INST             | child                       |
| MemberAccess                  | CALL(memberAccess)&METHOD_INST                     | child                       |
| CastExpression                | CALL(case)&METHOD_INST                             | CastTarget&Expression       |
| ArrayIndexin                  | CALL(computedMemberAccess)&METHOD_INST             | ArrayExp;IndexExp           |
| Label                         | UNKNOW                                             |                             |
| SizeofOperand                 | UNKNOW/Other                                       | Expression                  |
| SizeofExpression              | CALL(sizeOf)&METHOD_INST                           | child[1]                    |
| IdentifierDecl(*)             | TYPE_DECL; MEMBER;LOCAL                            | ;;Expression                |
| IdentifierDeclStatement       |                                                    | IdentifierDecl              |
| ReturnStatement               | RETURN                                             | Expression(opt.)            |
| CompoundStatement             | BLOCK                                              | Statement                   |
| ExpressionStatement           |                                                    | Expression                  |
| IfStatement                   | UNKNOW                                             | Condition&Statment&ElseStmt |
| ThrowStatement                | UNKNOW                                             | Expression                  |
| CatchList                     | UNKNOW                                             | CatchElement                |
| BlockStarter                  | UNKNOW                                             | child                       |
| ForInit                       |                                                    | child                       |
| Expression                    | BLOCK                                              | child                       |
| ConditionExpression           | Call(<operator>.conditionalExpression)&METHOD_INST | child                       |
| Condition                     |                                                    | Expression                  |
| Identifier                    | IDENTIFIER                                         |                             |
| GotoStatement                 | UNKNOW                                             |                             |
| ContinueStatement             | UNKNOW                                             |                             |
| BreakStatement                | UNKNOW                                             |                             |
| Constant                      | LITERAL                                            |                             |
| CallExpression                | CALL&METHOD_INST                                   | ArgumentList                |
| PostIncDecOperationExpression | CALL&METHOD_INST                                   | child                       |
| UnaryExpression               | CALL&METHOD_INST;UNKNOW(no child)                  |                             |
| And/xxx/xx/xxExpression       |                                                    | BinaryExpr                  |
| FunctionDefBase               | METHOD&METHOD_RETURN                               | ParameterList               |
|                               |                                                    |                             |

##### 2.2 CFG

CFG就很简单，添加CFG边就完事了。主要4 种类型的边：

True， False， Always,  Case

实现一个是layer，这个主要是标志作用域，比如for，while这种类型的statement会push一个layer

还有就是fringe结构，在每遍历一个CFG节点时，都会把fringe里的边添加到该节点上，比如fringe里是

[{BreakStatementNode: Always}]，那么在遍历下个节点时就会给该节点和BreakStatementNode添加一条Always边，所以fringe的作用就是存储下一个遍历节点的前驱。这里看一个for statement的实现：

```scala
override def visit(forStatement: ForStatement): Unit = {
    breakStack.pushLayer() // for 可能嵌套
    continueStack.pushLayer() 

    Option(forStatement.getForInitExpression).foreach(_.accept(this)) //先解析init，可能会有

    markerStack = None :: markerStack
    val conditionOption = Option(forStatement.getCondition) //解析condition
    val conditionFringe =
    conditionOption match {
        case Some(condition) =>
        condition.accept(this) 
        val storedFringe = fringe
        fringe = fringe.setCfgEdgeType(TrueEdge) // condition的true是body
        storedFringe
        case None =>
        fringe.empty()
    }

    forStatement.getStatement.accept(this) // 解析body，以便取出continue和break，body的后继是loop

    fringe = fringe.add(continueStack.getTopElements, AlwaysEdge) //continue的always是loop

    Option(forStatement.getForLoopExpression).foreach(_.accept(this)) // 解析loop

    markerStack.head.foreach(extendCfg) //不懂，没被赋值，应该是做一些特殊节点的处理

    fringe = conditionFringe
    .setCfgEdgeType(FalseEdge) //condition的false是exit
    .add(breakStack.getTopElements, AlwaysEdge) // break的always是exit

    markerStack = markerStack.tail
    breakStack.popLayer() //for结束
    continueStack.popLayer()
}
```





##### 2.3 CPG

开源版见图，这里列出的是所有边和节点，需要注意的是并不是所有边和节点都会用到

###### 2.3.1 Edge

 ```scala
public static final String ALIAS_OF = "ALIAS_OF";
public static final String AST = "AST";
public static final String ATTACHED_DATA = "ATTACHED_DATA";
public static final String BINDS_TO = "BINDS_TO";
public static final String CALL = "CALL";
public static final String CALL_ARG = "CALL_ARG";
public static final String CALL_ARG_OUT = "CALL_ARG_OUT";
public static final String CALL_RET = "CALL_RET";
public static final String CAPTURE = "CAPTURE";
public static final String CAPTURED_BY = "CAPTURED_BY";
public static final String CDG = "CDG";
public static final String CFG = "CFG";
public static final String CONTAINS = "CONTAINS";
public static final String CONTAINS_NODE = "CONTAINS_NODE";
public static final String DOMINATE = "DOMINATE";
public static final String EVAL_TYPE = "EVAL_TYPE";
public static final String INHERITS_FROM = "INHERITS_FROM";
public static final String IS_SENSITIVE_DATA_DESCR_OF = "IS_SENSITIVE_DATA_DESCR_OF";
public static final String IS_SENSITIVE_DATA_DESCR_OF_REF = "IS_SENSITIVE_DATA_DESCR_OF_REF";
public static final String IS_SENSITIVE_DATA_OF_TYPE = "IS_SENSITIVE_DATA_OF_TYPE";
public static final String PARAMETER_LINK = "PARAMETER_LINK";
public static final String POST_DOMINATE = "POST_DOMINATE";
public static final String PROPAGATE = "PROPAGATE";
public static final String REACHING_DEF = "REACHING_DEF";
public static final String RECEIVER = "RECEIVER";
public static final String REF = "REF";
public static final String RESOLVED_TO = "RESOLVED_TO";
public static final String TAGGED_BY = "TAGGED_BY";
public static final String TAINT_REMOVE = "TAINT_REMOVE";
public static final String VTABLE = "VTABLE";
 ```

###### 2.3.2 Node Key

```scala
public static final String ALIAS_TYPE_FULL_NAME = "ALIAS_TYPE_FULL_NAME";
public static final String ANNOTATION_FULL_NAME = "ANNOTATION_FULL_NAME";
public static final String ANNOTATION_NAME = "ANNOTATION_NAME";
public static final String ARGUMENT_INDEX = "ARGUMENT_INDEX";
public static final String AST_PARENT_FULL_NAME = "AST_PARENT_FULL_NAME";
public static final String AST_PARENT_TYPE = "AST_PARENT_TYPE";
public static final String BINARY_SIGNATURE = "BINARY_SIGNATURE";
public static final String CATEGORY = "CATEGORY";
public static final String CLASS_NAME = "CLASS_NAME";
public static final String CLOSURE_BINDING_ID = "CLOSURE_BINDING_ID";
public static final String CLOSURE_ORIGINAL_NAME = "CLOSURE_ORIGINAL_NAME";
public static final String CODE = "CODE";
public static final String COLUMN_NUMBER = "COLUMN_NUMBER";
public static final String COLUMN_NUMBER_END = "COLUMN_NUMBER_END";
public static final String CONTENT = "CONTENT";
public static final String DEPENDENCY_GROUP_ID = "DEPENDENCY_GROUP_ID";
public static final String DEPTH_FIRST_ORDER = "DEPTH_FIRST_ORDER";
public static final String DESCRIPTION = "DESCRIPTION";
public static final String DISPATCH_TYPE = "DISPATCH_TYPE";
public static final String EVALUATION_STRATEGY = "EVALUATION_STRATEGY";
public static final String EVALUATION_TYPE = "EVALUATION_TYPE";
public static final String FILENAME = "FILENAME";
public static final String FINGERPRINT = "FINGERPRINT";
public static final String FULL_NAME = "FULL_NAME";
public static final String HAS_MAPPING = "HAS_MAPPING";
public static final String INHERITS_FROM_TYPE_FULL_NAME = "INHERITS_FROM_TYPE_FULL_NAME";
public static final String IS_EXTERNAL = "IS_EXTERNAL";
public static final String IS_STATIC = "IS_STATIC";
public static final String LANGUAGE = "LANGUAGE";
public static final String LINE_NO = "LINE_NO";
public static final String LINE_NUMBER = "LINE_NUMBER";
public static final String LINE_NUMBER_END = "LINE_NUMBER_END";
public static final String LINK = "LINK";
public static final String LITERALS_TO_SINK = "LITERALS_TO_SINK";
public static final String METHOD_FULL_NAME = "METHOD_FULL_NAME";
public static final String METHOD_INST_FULL_NAME = "METHOD_INST_FULL_NAME";
public static final String METHOD_NAME = "METHOD_NAME";
public static final String METHOD_SHORT_NAME = "METHOD_SHORT_NAME";
public static final String MODIFIER_TYPE = "MODIFIER_TYPE";
public static final String NAME = "NAME";
public static final String NODE_ID = "NODE_ID";
public static final String NODE_LABEL = "NODE_LABEL";
public static final String ORDER = "ORDER";
public static final String PACKAGE_NAME = "PACKAGE_NAME";
public static final String PARAMETER = "PARAMETER";
public static final String PARAMETER_INDEX = "PARAMETER_INDEX";
public static final String PARSER_TYPE_NAME = "PARSER_TYPE_NAME";
public static final String PATH = "PATH";
public static final String PATTERN = "PATTERN";
public static final String RESOLVED = "RESOLVED";
public static final String SCC_ID = "SCC_ID";
public static final String SCORE = "SCORE";
public static final String SIGNATURE = "SIGNATURE";
public static final String SINK_TYPE = "SINK_TYPE";
public static final String SOURCE_TYPE = "SOURCE_TYPE";
public static final String SPID = "SPID";
public static final String SYMBOL = "SYMBOL";
public static final String TITLE = "TITLE";
public static final String TYPE_DECL_FULL_NAME = "TYPE_DECL_FULL_NAME";
public static final String TYPE_FULL_NAME = "TYPE_FULL_NAME";
public static final String VALUE = "VALUE";
public static final String VAR_TYPE = "VAR_TYPE";
public static final String VERSION = "VERSION";
public static final String VULN_DESCR = "VULN_DESCR";
```

###### 2.3.3 Node Type

```scala
public static final String ANNOTATION = "ANNOTATION";
public static final String ANNOTATION_LITERAL = "ANNOTATION_LITERAL";
public static final String ANNOTATION_PARAMETER = "ANNOTATION_PARAMETER";
public static final String ANNOTATION_PARAMETER_ASSIGN = "ANNOTATION_PARAMETER_ASSIGN";
public static final String ARRAY_INITIALIZER = "ARRAY_INITIALIZER";
public static final String BLOCK = "BLOCK";
public static final String CALL = "CALL";
public static final String CALL_CHAIN = "CALL_CHAIN";
public static final String CALL_SITE = "CALL_SITE";
public static final String CLOSURE_BINDING = "CLOSURE_BINDING";
public static final String COMMENT = "COMMENT";
public static final String CONFIG_FILE = "CONFIG_FILE";
public static final String DEPENDENCY = "DEPENDENCY";
public static final String DETACHED_TRACKING_POINT = "DETACHED_TRACKING_POINT";
public static final String FILE = "FILE";
public static final String FINDING = "FINDING";
public static final String FLOW = "FLOW";
public static final String FRAMEWORK = "FRAMEWORK";
public static final String FRAMEWORK_DATA = "FRAMEWORK_DATA";
public static final String IDENTIFIER = "IDENTIFIER";
public static final String IOFLOW = "IOFLOW";
public static final String LITERAL = "LITERAL";
public static final String LOCAL = "LOCAL";
public static final String LOCATION = "LOCATION";
public static final String MATCH_INFO = "MATCH_INFO";
public static final String MEMBER = "MEMBER";
public static final String META_DATA = "META_DATA";
public static final String METHOD = "METHOD";
public static final String METHOD_INST = "METHOD_INST";
public static final String METHOD_PARAMETER_IN = "METHOD_PARAMETER_IN";
public static final String METHOD_PARAMETER_OUT = "METHOD_PARAMETER_OUT";
public static final String METHOD_REF = "METHOD_REF";
public static final String METHOD_RETURN = "METHOD_RETURN";
public static final String METHOD_SUMMARY = "METHOD_SUMMARY";
public static final String MODIFIER = "MODIFIER";
public static final String NAMESPACE = "NAMESPACE";
public static final String NAMESPACE_BLOCK = "NAMESPACE_BLOCK";
public static final String PROGRAM_POINT = "PROGRAM_POINT";
public static final String READ = "READ";
public static final String RETURN = "RETURN";
public static final String ROUTE = "ROUTE";
public static final String SENSITIVE_DATA_TYPE = "SENSITIVE_DATA_TYPE";
public static final String SENSITIVE_MEMBER = "SENSITIVE_MEMBER";
public static final String SENSITIVE_REFERENCE = "SENSITIVE_REFERENCE";
public static final String SENSITIVE_VARIABLE = "SENSITIVE_VARIABLE";
public static final String SINK = "SINK";
public static final String SOURCE = "SOURCE";
public static final String SP_ANNOTATION_PARAMETER = "SP_ANNOTATION_PARAMETER";
public static final String SP_BLACKLIST = "SP_BLACKLIST";
public static final String TAG = "TAG";
public static final String TAGS = "TAGS";
public static final String TAG_NODE_PAIR = "TAG_NODE_PAIR";
public static final String TRANSFORM = "TRANSFORM";
public static final String TRANSFORMATION = "TRANSFORMATION";
public static final String TYPE = "TYPE";
public static final String TYPE_ARGUMENT = "TYPE_ARGUMENT";
public static final String TYPE_DECL = "TYPE_DECL";
public static final String TYPE_PARAMETER = "TYPE_PARAMETER";
public static final String UNKNOWN = "UNKNOWN";
public static final String VARIABLE_INFO = "VARIABLE_INFO";
public static final String WRITE = "WRITE";
public static final String PACKAGE_PREFIX = "PACKAGE_PREFIX";
```

######　2.3.4 Ｏperator

```scala
public static final String addition = "<operator>.addition";
public static final String subtraction = "<operator>.subtraction";
public static final String multiplication = "<operator>.multiplication";
public static final String division = "<operator>.division";
public static final String exponentiation = "<operator>.exponentiation";
public static final String modulo = "<operator>.modulo";
public static final String shiftLeft = "<operator>.shiftLeft";
public static final String logicalShiftRight = "<operator>.logicalShiftRight";
public static final String arithmeticShiftRight = "<operator>.arithmeticShiftRight";
public static final String not = "<operator>.not";
public static final String and = "<operator>.and";
public static final String or = "<operator>.or";
public static final String xor = "<operator>.xor";
public static final String assignmentPlus = "<operator>.assignmentPlus";
public static final String assignmentMinus = "<operator>.assignmentMinus";
public static final String assignmentMultiplication = "<operator>.assignmentMultiplication";
public static final String assignmentDivision = "<operator>.assignmentDivision";
public static final String assignmentExponentiation = "<operators>.assignmentExponentiation";
public static final String assignmentModulo = "<operators>.assignmentModulo";
public static final String assignmentShiftLeft = "<operators>.assignmentShiftLeft";
public static final String assignmentLogicalShifRight = "<operators>.assignmentLogicalShifRight";
public static final String assignmentArithmeticShiftRight = "<operators>.assignmentArithmeticShiftRight";
public static final String assignmentAnd = "<operators>.assignmentAnd";
public static final String assignmentOr = "<operators>.assignmentOr";
public static final String assignmentXor = "<operators>.assignmentXor";
public static final String assignment = "<operator>.assignment";
public static final String minus = "<operator>.minus";
public static final String plus = "<operator>.plus";
public static final String preIncrement = "<operator>.preIncrement";
public static final String preDecrement = "<operator>.preDecrement";
public static final String postIncrement = "<operator>.postIncrement";
public static final String postDecrement = "<operator>.postDecrement";
public static final String logicalNot = "<operator>.logicalNot";
public static final String logicalOr = "<operator>.logicalOr";
public static final String logicalAnd = "<operator>.logicalAnd";
public static final String equals = "<operator>.equals";
public static final String notEquals = "<operator>.notEquals";
public static final String greaterThan = "<operator>.greaterThan";
public static final String lessThan = "<operator>.lessThan";
public static final String greaterEqualsThan = "<operator>.greaterEqualsThan";
public static final String lessEqualsThan = "<operator>.lessEqualsThan";
public static final String instanceOf = "<operator>.instanceOf";
public static final String memberAccess = "<operator>.memberAccess";
public static final String indirectMemberAccess = "<operator>.indirectMemberAccess";
public static final String computedMemberAccess = "<operator>.computedMemberAccess";
public static final String indirectComputedMemberAccess = "<operator>.indirectComputedMemberAccess";
public static final String indirection = "<operator>.indirection";
public static final String delete = "<operator>.delete";
public static final String conditional = "<operator>.conditional";
public static final String cast = "<operator>.cast";
public static final String compare = "<operator>.compare";
public static final String addressOf = "<operator>.addressOf";
public static final String sizeOf = "<operator>.sizeOf";
```

###### 2.3.5 开源版的validation

用来验证导入的cpg是否正确

```scala
NodeTypes.FILE has 0 to N outgoing EdgeTypes.AST to NodeTypes.NAMESPACE_BLOCK,
NodeTypes.METHOD has 1 outgoing EdgeTypes.AST to NodeTypes.METHOD_RETURN,
NodeTypes.METHOD has 0 to N outgoing EdgeTypes.AST to NodeTypes.METHOD_PARAMETER_IN,
NodeTypes.METHOD has 0 to N outgoing EdgeTypes.AST to NodeTypes.MODIFIER,
NodeTypes.METHOD has 1 outgoing EdgeTypes.AST to NodeTypes.BLOCK,
NodeTypes.METHOD has 0 to N outgoing EdgeTypes.AST to NodeTypes.TYPE_PARAMETER,
NodeTypes.METHOD has 0 to N outgoing EdgeTypes.AST to NodeTypes.ANNOTATION,
NodeTypes.METHOD has 0 to N outgoing EdgeTypes.CFG to SuperTypes.Expression,
NodeTypes.METHOD_PARAMETER_IN has 0 to N outgoing EdgeTypes.AST to NodeTypes.ANNOTATION,
NodeTypes.TYPE has 0 to N outgoing EdgeTypes.AST to NodeTypes.TYPE_ARGUMENT,
NodeTypes.TYPE_DECL has 0 to N outgoing EdgeTypes.AST to NodeTypes.TYPE_PARAMETER,
NodeTypes.TYPE_DECL has 0 to N outgoing EdgeTypes.AST to NodeTypes.MEMBER,
NodeTypes.TYPE_DECL has 0 to N outgoing EdgeTypes.AST to NodeTypes.MODIFIER,
NodeTypes.TYPE_DECL has 0 to N outgoing EdgeTypes.AST to NodeTypes.ANNOTATION,
NodeTypes.TYPE_DECL has 0 to N outgoing EdgeTypes.VTABLE to NodeTypes.METHOD,
NodeTypes.MEMBER has 0 to N outgoing EdgeTypes.AST to NodeTypes.ANNOTATION,
NodeTypes.MEMBER has 0 to N outgoing EdgeTypes.AST to NodeTypes.MODIFIER,
NodeTypes.LITERAL has 1 to N outgoing EdgeTypes.CFG to SuperTypes.Expression or NodeTypes.METHOD_RETURN,
NodeTypes.CALL has 0 to N outgoing EdgeTypes.AST to SuperTypes.Expression,
NodeTypes.CALL has 1 to N outgoing EdgeTypes.CFG to SuperTypes.Expression or NodeTypes.METHOD_RETURN,
NodeTypes.CALL has 0 to 1 outgoing EdgeTypes.RECEIVER to
NodeTypes.CALL or NodeTypes.IDENTIFIER or NodeTypes.LITERAL or NodeTypes.METHOD_REF or NodeTypes.BLOCK,
NodeTypes.IDENTIFIER has 1 to N outgoing EdgeTypes.CFG to SuperTypes.Expression,
NodeTypes.IDENTIFIER has 0 to 1 outgoing EdgeTypes.REF to NodeTypes.LOCAL or NodeTypes.METHOD_PARAMETER_IN,
NodeTypes.RETURN has 0 to 1 outgoing EdgeTypes.AST to SuperTypes.Expression,
NodeTypes.RETURN has 1 outgoing EdgeTypes.CFG to NodeTypes.METHOD_RETURN,
NodeTypes.BLOCK has 0 to N outgoing EdgeTypes.AST to SuperTypes.Expression,
NodeTypes.BLOCK has 0 to N outgoing EdgeTypes.AST to NodeTypes.LOCAL,
NodeTypes.BLOCK has 0 to N outgoing EdgeTypes.CFG to SuperTypes.Expression or NodeTypes.METHOD_RETURN,
NodeTypes.METHOD_INST has 0 to N outgoing EdgeTypes.AST to NodeTypes.TYPE_ARGUMENT,
NodeTypes.METHOD_REF has 1 to N outgoing EdgeTypes.CFG to SuperTypes.Expression,
NodeTypes.METHOD_REF has 0 to N outgoing EdgeTypes.CAPTURE to NodeTypes.CLOSURE_BINDING,
NodeTypes.CLOSURE_BINDING has 1 outgoing EdgeTypes.REF to NodeTypes.LOCAL or NodeTypes.METHOD_PARAMETER_IN
```

###### 2.3.6 边的作用

打x表示开源版未用到（有些是定义了没用到）

| Edge                           | 作用                                                         | 涉及的算法                   |
| ------------------------------ | ------------------------------------------------------------ | ---------------------------- |
| ALIAS_OF                       | Alias relation between types，link时生成                     |                              |
| AST                            | AST转换成CPG的时候生成的边，标志AST关系                      |                              |
| ATTACHED_DATA                  | x                                                            |                              |
| BINDS_TO                       | Type argument binding to a type parameter(x)                 |                              |
| CALL                           | call->methodInst，A (method)-call                            |                              |
| CALL_ARG                       | x                                                            |                              |
| CALL_ARG_OUT                   | x                                                            |                              |
| CAPTURE                        | Connection between a captured LOCAL and the corresponding CLOSURE_BINDING（x） |                              |
| CDG                            | x                                                            |                              |
| CFG                            | Control Flow Graph                                           | 见CFG                        |
| CONTAINS                       | Shortcut over multiple AST edges，比如block下面包含的所有statement都会有一条CONTAINS边 | 遍历就完事                   |
| CONTAINS_NODE                  | Membership relation for a compound object（x）               |                              |
| DOMINATE                       | x                                                            |                              |
| EVAL_TYPE                      | Link to evaluation type，对应各种类型的节点到TYPE节点        |                              |
| INHERITS_FROM                  | Inheritance relation between types                           |                              |
| IS_SENSITIVE_DATA_DESCR_OF     | x                                                            |                              |
| IS_SENSITIVE_DATA_DESCR_OF_REF | x                                                            |                              |
| IS_SENSITIVE_DATA_OF_TYPE      | x                                                            |                              |
| PARAMETER_LINK                 | Links together corresponding METHOD_PARAMETER_IN and METHOD_PARAMETER_OUT nodes |                              |
| POST_DOMINATE                  | x                                                            |                              |
| PROPAGATE                      | Encodes propagation of data from on node to another，这个边标志数据流的传播关系，人工定义，会在数据流分析中仔细分析 |                              |
| REACHING_DEF                   | Reaching definition edge，连接变量使用和定义位置，数据流分析的核心 | Reaching Definition Analysis |
| RECEIVER                       | The receiver of a method call which is either an object or a pointer(x) |                              |
| REF                            | A reference to e.g. a LOCAL，在多个地方均会创建REF边，标志一种引用关系 |                              |
| RESOLVED_TO                    | x                                                            |                              |
| TAGGED_BY                      | Edges from nodes to tags（x）                                |                              |
| TAINT_REMOVE                   | x                                                            |                              |
| VTABLE                         | Indicates that a method is part of the vtable of a certain type declaratio |                              |





#### 4. 后端实现

##### 4.1 call-graph分析

joern未实现

##### 4.2 数据流分析

开源版数据流分析就一个Reaching Definition Analysis。该算法表述为

- ![{{\rm {REACH}}}_{{{\rm {in}}}}[S]=\bigcup _{{p\in pred[S]}}{{\rm {REACH}}}_{{{\rm {out}}}}[p]](https://wikimedia.org/api/rest_v1/media/math/render/svg/88d90a4df4dc3fd7d5f07094e1c86593b00e56c2)

  ​							![{{\rm {REACH}}}_{{{\rm {out}}}}[S]={{\rm {GEN}}}[S]\cup ({{\rm {REACH}}}_{{{\rm {in}}}}[S]-{{\rm {KILL}}}[S])](https://wikimedia.org/api/rest_v1/media/math/render/svg/fc62c65a0828cf46b792d3a87e3cffc4161382f0)



S指的CFG中的节点（粒度可粗可细，一般来说是用basic block作为S，但joern处理略有不同），REACH_in 指的是到达该节点的定义集合，Reach_out 指的是从该节点传递出去的定义，p是指S的前驱。

所以第一个公式很好理解，S的REACH_in 就是它前驱的REACH_out的并。

第二个公式就要先介绍一下GEN和KILL，举个例子

1: int a = 1;

2: a=2;

两个a分别用a_1和a_2表示

假如1和2属不同S，REACH_in[1] = ∅，REACH_out[1] = GEN[1]∪(REACH_in[1] - KILL[1]) = {a_1}∪{∅-{a_2}} = {a_1}

GEN代表生成的定义，比如赋值操作就是定义了一个变量。

然后看2，REACH_in[2] = REACH_out[1] =  {a_1}，注意下这里a_2和a_1同指向一个变量，所以a_2 kill 了a_1，道理很简单，接下来如果有a的使用，那么一定是使用的a_2而不是a_1，同时a_1也kill了a_2，如果控制流从2返回到1，那么很明显情况就倒过来了。在例子这个情况下 KILL[2] = {a_1}，然后可以算出REACH_out[2] = {a_2}，这个是比较简单的情况，然后结合joern来分析三条语句以上的情况。

假设我们有四条语句



S1: a = 1;       a_1

S2: b = a;       b_1 a_2

S3: a = 3;       a_3

S4: b = a;       b_2 a_4

还是老规矩先标号，然后按cpg的思路来分析，为了方便，这里去除了定义，这个a有可能是函数参数，也可能是一般变量

首先在cpg中什么操作都是method，这个图可以画成这样

###### 图

见附件的png

###### 框架

有了这个图就可以来看Reaching Definition的代码了。首先看一个大框架，基本和上面描述的算法相似

```scala
class ReachingDefPass(graph: ScalaGraph) extends CpgPass(graph) {
  var dfHelper: DataFlowFrameworkHelper = _

  override def run(): Iterator[DiffGraph] = {
    val dstGraph = new DiffGraph()
    val methods = graph.V.hasLabel(NodeTypes.METHOD).l

    dfHelper = new DataFlowFrameworkHelper(graph)

    methods.foreach { method => // 对所有method分析
      var worklist = Set[Vertex]()
      var out = Map[Vertex, Set[Vertex]]().withDefaultValue(Set[Vertex]()) // in
      var in = Map[Vertex, Set[Vertex]]().withDefaultValue(Set[Vertex]()) // out
      val allCfgNodes = ExpandTo.allCfgNodesOfMethod(method).toList

      val mapExpressionsGens = dfHelper.expressionsToGenMap(method).withDefaultValue(Set[Vertex]()) // 这边针对method中每个cfg节点生成gen（因为每个cfg节点的赋值操作由于call的原因，如果有，那么只可能有一个。也就不用考虑单个基本块的KILL问题-多条赋值语句可能会直接kill掉一些gen）
      val mapExpressionsKills = dfHelper.expressionsToKillMap(method).withDefaultValue(Set[Vertex]()) // 生成kill

      /*Initialize the OUT sets*/
      allCfgNodes.foreach { cfgNode =>
        if (mapExpressionsGens.contains(cfgNode)) {
          out += cfgNode -> mapExpressionsGens(cfgNode) // 初始化out列表为gen，接下来就∪(in-kill)就行
        }
      }
      // worklist算法，就是那两个公式
      worklist ++= allCfgNodes 

      while (worklist.nonEmpty) {
        var currentCfgNode = worklist.head
        worklist = worklist.tail

        var inSet = Set[Vertex]()

        val cfgPredecessors = currentCfgNode.vertices(Direction.IN, EdgeTypes.CFG).asScala
        cfgPredecessors.foreach { pred =>
          inSet ++= inSet.union(out(pred)) // 公式1
        }

        in += currentCfgNode -> inSet

        val oldSize = out(currentCfgNode).size
        val gens = mapExpressionsGens(currentCfgNode) // 获取gens，那么其实前面都生成完了，只要获取当前节点的gen就行了
        val kills = mapExpressionsKills(currentCfgNode) // 同理
        out += currentCfgNode -> gens.union(inSet.diff(kills)) // 公式2
        val newSize = out(currentCfgNode).size

        if (oldSize != newSize) // 观察out有没有变化
          worklist ++= currentCfgNode.vertices(Direction.OUT, EdgeTypes.CFG).asScala.toList
      }

      addReachingDefEdge(dstGraph, method, out, in)  // 利用in和out生成REACH_DEF边，构建define 和use 的关系
    }

    Iterator(dstGraph)
  }
```



看完大体流程，就是三个核心部分，gen和kill怎么生成，然后addReachingDefEdge做了啥？

先看gen和kill。

###### gen

``` scala
  def expressionsToGenMap(methodVertex: Vertex): Map[Vertex, Set[Vertex]] = {
    /*genExpressions correspond to call assignment nodes*/
    val genExpressions = getExpressions(methodVertex) //这里获取了method中的所有call expression，注意到所有操作都是method，这很自然（每个call都是一个单独的cfg节点）
    genExpressions.map { genExpression =>
      genExpression -> getGensOfExpression(genExpression) // 对每个call expression生成 gen
    }.toMap
  }
```



继续看getGensOfExpression

```scala
  def getGensOfExpression(expr: Vertex): Set[Vertex] = {
    var gens = Set[Vertex]()
    val methodParamOutsOrder = callToMethodParamOut(expr)
      .filter(methPO => methPO.edges(Direction.IN, EdgeTypes.PROPAGATE).hasNext)
      .map(_.value2(NodeKeys.ORDER).toInt) // 找到call对应的method的paramter out，然后找到有propagate边的节点对应的参数的位置（有点拗口）

    val identifierWithOrder =
      filterArgumentIndex(expr.vertices(Direction.OUT, EdgeTypes.AST).asScala.toList, methodParamOutsOrder) // 找到这个参数在call参数中的位置
    gens ++= identifierWithOrder

    gens
  }
```

这么看就是paramterout的propagate边至关重要。可以看看它是怎么生成的

```scala
  override def run(): Iterator[DiffGraph] = {
    dstGraph = new DiffGraph()

    semantics.elements.foreach { semantic => //semantics从哪来？？？
      val methodOption =
        graph.V().hasLabel(NodeTypes.METHOD).has(NodeKeys.FULL_NAME -> semantic.methodFullName).headOption()

      methodOption match {
        case Some(method) =>
          addSelfDefSemantic(method, semantic.parameterIndex)
        case None =>
      }
    }

    Iterator(dstGraph)
  }
```

注意下这个addSelfDefSemantic

``` scala
  private def addSelfDefSemantic(method: Vertex, parameterIndex: Int): Unit = {
    // From where the PROPAGATE edge is coming does not matter for the open source reachable by.
    // Thus we let it start from the corresponding METHOD_PARAMETER_IN.
    val parameterInOption = method
      .vertices(Direction.OUT, EdgeTypes.AST)
      .asScala
      .find(node => node.label() == NodeTypes.METHOD_PARAMETER_IN && node.value2(NodeKeys.ORDER) == parameterIndex)

    val parameterOutOption = method
      .vertices(Direction.OUT, EdgeTypes.AST)
      .asScala
      .find(node => node.label() == NodeTypes.METHOD_PARAMETER_OUT && node.value2(NodeKeys.ORDER) == parameterIndex)

    (parameterInOption, parameterOutOption) match {
      case (Some(parameterIn), Some(parameterOut)) =>
        addPropagateEdge(parameterIn, parameterOut, isAlias = false)
      case (None, _) =>
        logger.warn(s"Could not find parameter $parameterIndex of ${method.value2(NodeKeys.FULL_NAME)}.")
      case _ =>
        logger.warn(s"Could not find output parameter $parameterIndex of ${method.value2(NodeKeys.FULL_NAME)}.")
    }
  }
```

就是对应的METHOD_PARAMETER_OUT和METHOD_PARAMETER_IN节点间连了一条对应参数index的边？？？那么还是上面那个问题semantics是哪来的。，这个semantics是自己定义的。

joern中有一个semantics loader

```scala
class SemanticsLoader(filename: String) {
  private val logger = LogManager.getLogger(getClass)

  def load(): Semantics = {
    val bufferedReader = Source.fromFile(filename)
    var lineNumber = 0

    try {
      val semanticElements =
        bufferedReader
          .getLines()
          .flatMap { line =>
            val parts = line.split(",")

            if (parts.size == 2) {
              try {
                val methodFullName = parts(0).trim
                val parameterIndex = parts(1).trim.toInt
                lineNumber += 1
                Some(Semantic(methodFullName, parameterIndex))
              } catch {
                case _: NumberFormatException =>
                  logFormatError("Argument index is not convertable to Int.", lineNumber)
                  None
              }

            } else {
              logFormatError("Invalid number of elements per line. Expected method name followed by argument index.",
                             lineNumber)
              None
            }
          }
          .toList

      Semantics(semanticElements)
    } finally {
      bufferedReader.close()
    }

  }
```

这样通过指定method和参数index来暗示该在哪里添加propagate边。但我没找到对应的文件，但大概也能猜到了。类似赋值这种操作都会在对应参数的位置标上一条propagate边来表示它是存在这种赋值语义的。joern针对c的话也有可能会处理一些传入引用进去的函数，这样那个值就是可变的，也就是左值。这种情况会添加一条propagate边。上图我标红色的就是该边

那知道了propagate怎么生成的，接下来继续看

```scala
  def getGensOfExpression(expr: Vertex): Set[Vertex] = {
    var gens = Set[Vertex]()
    val methodParamOutsOrder = callToMethodParamOut(expr)
      .filter(methPO => methPO.edges(Direction.IN, EdgeTypes.PROPAGATE).hasNext)
      .map(_.value2(NodeKeys.ORDER).toInt) // 找到call对应的method的paramter out，然后找到有propagate边的节点对应的参数的位置（有点拗口）
    //所以这里实际是获取了会发生变化的参数值
    val identifierWithOrder =
      filterArgumentIndex(expr.vertices(Direction.OUT, EdgeTypes.AST).asScala.toList, methodParamOutsOrder) // 找到这个参数在call参数中的位置
    gens ++= identifierWithOrder //把这个参数添加到gen。 

    gens
  }
```

比如赋值操作，那么它的第一个参数id会被放入gens。



gen分析完了，接下来分析kill

###### kill

```scala
  def expressionsToKillMap(methodVertex: Vertex): Map[Vertex, Set[Vertex]] = {
    val genExpressions = getExpressions(methodVertex) // 获取call
    val genSet = getGenSet(methodVertex) // 没用到。。。可能是typo

    genExpressions.map { expression =>
      val gens = getGensOfExpression(expression)  // 获取call的gen  
      expression -> kills(gens) // kill
    }.toMap
  }
```

来看看这个核心的kill实现

```scala
  def killsVertices(vertex: Vertex): Set[Vertex] = {
    // vertex: gen(id)
    val localRefIt = vertex.vertices(Direction.OUT, EdgeTypes.REF).asScala // 获取ref，那么id的ref就是local或者函数参数
    if (!localRefIt.hasNext) {
      Set() // 无
    } else {
      val localRef = localRefIt.next
      localRef.vertices(Direction.IN, EdgeTypes.REF).asScala.filter(_.id != vertex.id).toSet // 查找所有进来的ref边，除去对应id的节点
    }
  }

  def kills(vertex: Set[Vertex]): Set[Vertex] = {
    vertex.map(v => killsVertices(v)).fold(Set())((v1, v2) => v1.union(v2))
  }
```

这个可能有点难懂，配合图来看。结合GEN来整个的梳理一下

这个localRefIt就是a和b变量，这两个变量可能是local，也可能是函数的参数。（local是定义语句的情况）

首先对S1，KILL[S1] 首先找它的GEN，显然它的GEN是{a_1}，然后进到killsVertices，获取到REF，killsVertices的返回就是{a_2,a_3,a_4}，那么它的REACH_in[1] 就是 ∅， REACH_out[1] = {a_1} ∪(∅-{a_2,a_3,a_4}) = {a_1}

接下来看REACH_in[2] = {a_1}，然后算GEN[2]，注意这时候GEN涉及到两个变量，一个是b_1，一个是a_2。但a_2对应的index没有propagate边，所以这里GEN[2] = {b_1}，这时候算KILL[2] = {b_2}，REACH_out[2] = {b_1}∪({a_1} - {b_2}) = {a_1, b_1}

然后是REACH_in[3] = {a_1, b_1}, GEN[3] = {a_3}, KILL[3] = {a_1, a_2, a_4}, REACH_out[3] = {a_3}∪({a_1,b_1} - {a_1,a_2,a_4}) = {a_3,b_1}

同理REACH_in[4] = {a_3, b_1}, GEN[3] = {b_2}, KILL[4] = {b1}, REACH_out[3] = {b_2}∪({a_3,b_1} - {b_1}) = {a_3,b_2}

通过这样一个过程，基本就能把REACH_in和REACH_out都算出来

###### REACH_DEF

注意下我们上面算出来的in和out都是针对单个cfg节点的，在cpg中就是单个call。这样很容易构造使用链。来看看开源版的实现(可能有点长) 传入的参数分别为method（当前分析的method），outset（每个cfg节点对应的out id），inset（每个cfg节点对应的in id）

```scala
private def addReachingDefEdge(dstGraph: DiffGraph,
                                 method: Vertex,
                                 outSet: Map[Vertex, Set[Vertex]],
                                 inSet: Map[Vertex, Set[Vertex]]): Unit = {
	// 添加Reachdef边
    def addEdge(v0: Vertex, v1: Vertex): Unit = {
      dstGraph.addEdgeInOriginal(v0.asInstanceOf[nodes.StoredNode],
                                 v1.asInstanceOf[nodes.StoredNode],
                                 EdgeTypes.REACHING_DEF)
    }
    // paramter def use
    method.vertices(Direction.OUT, EdgeTypes.AST).asScala.filter(_.isInstanceOf[nodes.MethodParameterIn]).foreach {
      methodParameterIn =>
        // 对于每个method的参数，找到进来的ref，就是比如上面例子的a和b，如果他们是参数的话就会进到这。
        methodParameterIn.vertices(Direction.IN, EdgeTypes.REF).asScala.foreach { refInIdentifier =>
          dfHelper
            .getOperation(refInIdentifier)
            // 每个引用的method都加上 REACH_DEF
            .foreach(operationNode => addEdge(methodParameterIn, operationNode))
        }
    }

    val methodReturn = ExpandTo.methodToFormalReturn(method)

    
    ExpandTo
      .formalReturnToReturn(methodReturn)
       // method 的返回节点和methodReturn也需要建立REACH_DEF边
      .foreach(returnVertex => addEdge(returnVertex, methodReturn))

    outSet.foreach {
      // 例子里的node都是call，outDefs都是id
      case (node, outDefs) =>
        // 最常见的是call
        if (node.isInstanceOf[nodes.Call]) {
          // 注意这边拿use了，其实也很简单，去掉gen的部分就都是use，就比如例子中的a_2, a_4, 它们就是use
          /*
            def getUsesOfExpression(expr: Vertex): Set[Vertex] = {
                expr
                  .vertices(Direction.OUT, EdgeTypes.AST)
                  .asScala
                  .filter(!getGensOfExpression(expr).contains(_))
                  .toSet
              }
          */
          val usesInExpression = dfHelper.getUsesOfExpression(node)
          // 获取use的ref
          var localRefsUses = usesInExpression.map(ExpandTo.reference(_)).filter(_ != None)

          /* if use is not an identifier, add edge, as we are going to visit the use separately */
          usesInExpression.foreach { use =>
            if (!use.isInstanceOf[nodes.Identifier] && !use.isInstanceOf[nodes.Literal]) {
              // 特殊情况
              addEdge(use, node)

              /* handle indirect access uses: check if we have it in our out set and get
               * the corresponding def expression from which the definition reaches the use
               */
              if (isIndirectAccess(use)) {
                outDefs.filter(out => isIndirectAccess(out)).foreach { indirectOutDef =>
                  if (indirectOutDef.asInstanceOf[nodes.Call].code == use.asInstanceOf[nodes.Call].code) {
                    val expandedToCall = ExpandTo.argumentToCallOrReturn(indirectOutDef)
                    addEdge(expandedToCall, use)
                  }
                }
              }
            }
          }
          // operate or assignment 处理类似+=的操作，注意+= 数据流并不是中断了的！！！
          val nodeIsOperandAssignment = isOperationAndAssignment(node)
          if (nodeIsOperandAssignment) {
            // localRef，什么意思呢，先获取gen的ref，然后看看gen的ref包不包含inset中的元素，因为这边要维护数据流，也算一种特殊情况，这条REACH_DEF边都是gen产生的
            val localRefGens = dfHelper.getGensOfExpression(node).map(ExpandTo.reference(_))
            inSet(node)
              .filter(inElement => localRefGens.contains(ExpandTo.reference(inElement)))
              .foreach { filteredInElement =>
                // 两个call之间会有一条REACH_DEF边
                val expr = dfHelper.getExpressionFromGen(filteredInElement).foreach(addEdge(_, node))
              }
          }
          for (elem <- outDefs) {
            // 遍历每个out id，找到ref
            val localRefGen = ExpandTo.reference(elem)
            // 通过ast找到每个out id的call
            dfHelper.getExpressionFromGen(elem).foreach { expressionOfElement =>
              // 对于每个不是自己的call，如果use ref中包含out id的ref(用了！)，那就添加REACH_DEF边
              if (expressionOfElement != node && localRefsUses.contains(localRefGen)) {
                addEdge(expressionOfElement, node)
              }
            }
          }
        } else if (node.isInstanceOf[nodes.Return]) {
          // return is special case
          // 如果返回值，那么返回的Expression中也会有指向对应id的REACH_DEF边
          node.vertices(Direction.OUT, EdgeTypes.AST).asScala.foreach { returnExpr =>
            val localRef = ExpandTo.reference(returnExpr)
            inSet(node).filter(inElement => localRef == ExpandTo.reference(inElement)).foreach { filteredInElement =>
              dfHelper.getExpressionFromGen(filteredInElement).foreach(addEdge(_, node))
            }
          }
        }
    }
  }
```

这就是整个REACH_DEF了，从参数开始一直到返回值，中间涉及到很多call，这就是整个的数据流框架。

就我们的例子看



S1: a = 1;       a_1

S2: b = a;       b_1 a_2

S3: a = 3;       a_3

S4: b = a;       b_2 a_4

S1 REACH_in[1] = ∅ , GEN:{a_1}，KILL:{a_2,a_3,a_4}， REACH_out[1] = {a_1} ∪(∅-{a_2,a_3,a_4}) = {a_1}

S2 REACH_in[2] = {a_1}，GEN[2] = {b_1}，KILL[2] = {b_2}，REACH_out[2] = {b_1}∪({a_1} - {b_2}) = {a_1, b_1}

S3 REACH_in[3] = {a_1, b_1}, GEN[3] = {a_3}, KILL[3] = {a_1, a_2, a_4}, REACH_out[3] = {a_3}∪({a_1,b_1} - {a_1,a_2,a_4}) = {a_3,b_1}

S4 REACH_in[4] = {a_3, b_1}, GEN[3] = {b_2}, KILL[4] = {b1}, REACH_out[4] = {b_2}∪({a_3,b_1} - {b_1}) = {a_3,b_2}

那么分析从获取use开始

S1: ∅

S2: {a}

S3: ∅

S4: {a}

然后遍历outSet

S1 不会发生啥

S2 进到分支 

```scala
          for (elem <- outDefs) {
            // 遍历每个out id，找到ref
            val localRefGen = ExpandTo.reference(elem)
            // 通过ast找到每个out id的call
            dfHelper.getExpressionFromGen(elem).foreach { expressionOfElement =>
              // 对于每个不是自己的call，如果use ref中包含out id的ref(用了！)，那就添加REACH_DEF边
              if (expressionOfElement != node && localRefsUses.contains(localRefGen)) {
                addEdge(expressionOfElement, node)
              }
            }
          }
```

REACH_out[2] = {a_1, b_1}， localRefGens = [{a}, {b}]

expressionOfElement = {S1, S2}, node = S2。localRefGens[1] ∈ localRefsUses = {a}

addEdge(S1, S2)

S3 没进分支

S4 还是进到分支

```scala
          for (elem <- outDefs) {
            // 遍历每个out id，找到ref
            val localRefGen = ExpandTo.reference(elem)
            // 通过ast找到每个out id的call
            dfHelper.getExpressionFromGen(elem).foreach { expressionOfElement =>
              // 对于每个不是自己的call，如果use ref中包含out id的ref(用了！)，那就添加REACH_DEF边
              if (expressionOfElement != node && localRefsUses.contains(localRefGen)) {
                addEdge(expressionOfElement, node)
              }
            }
          }
```

REACH_out[4] = {a_3,b_2}， localRefGens = [{a}, {b}]

expressionOfElement = {S3, S4}, node = S4。localRefGens[1] ∈ localRefsUses = {a}

addEdge(S3, S4)

所以综上S1到S2会有一条，S3到S4会有一条，暗示定义和使用的关系

那么数据流就到这了，剩下就是递归遍历就能找到定义和使用的地方以及进过的call和method



##### 4.3 数据存储

图数据库，这个先放放

##### 4.4 查询

数据查询官方给的几种方法：

https://github.com/ShiftLeftSecurity/codepropertygraph

其中列了很多类型：

```scala
cpg.literal.toList
cpg.file.toList
cpg.namespace.toList
cpg.types.toList
cpg.methodReturn.toList
cpg.parameter.toList
cpg.member.toList
cpg.call.toList
cpg.local.toList
cpg.identifier.toList
cpg.argument.toList
cpg.typeDecl.toList
cpg.method.toList
cpg.methodInstance.toList
```

查询有两种方式。一种是形如 

> ./joern-query -f ./foo.scala

这个-f用来加载一个scala文件，其中foo.scala被安置于一个上下文：

```scala
cpg.file.name.l.mkString("\n")
```

其实有点类似spark-shell，cpg代表以及load到内存中的cpg，用这个作为起点，最后返回值取最后一次eval的值，比如

```
cpg.file.name.l.mkString("\n")
"a"
```

等执行完就会输出a。

另一种查询方式如

> ./joern-query -c cpg.file.name

这种方式的查询他们还搞了一大堆插件，其实都一样，其实就是拼接了一下而已：

``` scala
package io.shiftleft.joern

import java.io.FileReader
import javax.script.ScriptEngineManager

object JoernQuery extends App {

  parseConfig.foreach { config =>
    val e = new ScriptEngineManager().getEngineByName("scala")

    val cpgLoadingCode =
      s"""
      | import io.shiftleft.joern.CpgLoader
      | import io.shiftleft.codepropertygraph.Cpg
      | val cpg : Cpg = CpgLoader.load("${config.cpgFilename}")
      |""".stripMargin

    val context = e.getContexta
    e.eval(cpgLoadingCode, context)

    if (config.isFile) {
      val reader = new FileReader(config.query)
      println(e.eval(reader, context))
    } else {
      val script = config.query + ".l.mkString(\"\\n\")"
      println(e.eval(script, context))
    }
  }

  case class Config(cpgFilename: String, query: String, isFile: Boolean = false)
  def parseConfig: Option[Config] =
    new scopt.OptionParser[Config]("joern-query") {
      opt[String]('c', "cpg")
        .text("the code property graph to run queries on (default: cpg.bin.zip)")
        .action((x, c) => c.copy(cpgFilename = x))
      arg[String]("<query|filename>")
        .text("query to execute, or script file to execute if -f is set")
        .action((x, c) => c.copy(query = x))
      opt[Unit]('f', "isFile")
        .text("if specified, the second parameter is assumed to be a script file")
        .action((_, c) => c.copy(isFile = true))
    }.parse(args, Config("cpg.bin.zip", ""))

}

```

就像这样，所以这边实际就是可以根据它提供的cpg自己写scala处理

io.shiftleft.codepropertygraph.Cpg 也就是这个类

来看看

源码：

```c
#include <stdlib.h>
struct node {
    int value;
    struct node *next;
};

void free_list(struct node *head) {
    struct node *q;
    for (struct node *p = head; p != NULL; p = q) {
        q = p->next;
        free(p);
    }
}
```

使用cpg.method.name.toSet查询

```scala

Set(free_list, <operator>.indirectMemberAccess, <operator>.notEquals, <operator>.assignment, free)

```

cpg.file.namespaceBlock.name.toSet

```scala
Set(<global>)
```

cpg.file.typeDecl.name.toSet

```scala
Set(node)
```

cpg.method.name("free_list").parameter.name.toSet

```
Set(head)
```

cpg.typeDecl.name.toSet

```scala
Set(ANY, void, int, struct node *, node)
```

cpg.method.name("free_list").local.name

```scala
Set(q, p)
```

cpg.method.name("free_list").caller

call graph查找
查找调用者和被调用者(joern无法使用，但Ocular可以)



dataflow 分析
指定变量是否能到达控制流的某个点

指定一个souce，指定一个sink

```scala
souce = cpg.method.fullName(".*Http.*").methodReturn  // 函数名符合该正则表达式的函数的返回值
souce也可以是变量：
souce = cpg.identifier
sink = cpg.method("xxx").parameter

sink.reachableBy(source).flows.passesNot(".*filter.*").p // 会打印出数据流，同时可以指定filter，用于可能会有的过滤函数
```

目前来看就是最多就做到这样了。


