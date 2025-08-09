# 规则引擎


## 1. 背景
目前随着业务发展已经系统复杂性增加，需要使用规则引擎来管理一些业务上的规则，用于动态配置（因为本着配置优于代码）。
这边急需一套规则引擎来达到以下要求与目的：
* 动态配置规则，无须复杂的代码开发
* 规则易懂，不仅开发还是产品与测试都能理解并且能配置规则
* 能够创建独立的标签库，进而配置规则表达式，然后根据这些标签与表达式进行组合，达到复用的效果。

## 2.技术选型
市面上开源的规则表达式主流有[aviator](https://github.com/killme2008/aviatorscript)，以及[easy-rules](https://github.com/j-easy/easy-rules),这两个开源框架轻量，简单明了，适合我们绝大部分场景。Aviator 和 Easy Rules 都是规则引擎/表达式引擎，但它们的定位和功能侧重点有所不同。以下是两者的主要对比：
### 2.1 表达式能力对比
#### Aviator
1. 更丰富的运算符：支持算术、逻辑、位运算、集合操作等完整运算符集
2. 更强的计算能力：内置数学函数、字符串处理、正则表达式等
3. 类型系统：支持动态类型推断，对数字、字符串、集合等有良好支持
4. 性能优化：编译执行，性能较高
5. 适合场景：复杂表达式计算、数据转换、条件判断等

#### Easy Rules
1. 更简单的条件表达式：主要支持基本的逻辑判断
2. 规则优先：专注于规则的定义和执行流程
3. 轻量级：表达式能力相对简单，但规则管理更系统化
4. 适合场景：业务规则管理、决策表实现等

### 2.2 主要差异
| 特性         | Aviator                      | Easy Rules                   |
|--------------|------------------------------|------------------------------|
| **核心定位** | 表达式求值引擎               | 规则引擎框架                 |
| **表达式复杂度** | 高，支持复杂运算          | 中，侧重条件判断             |
| **执行性能** | 高(编译执行)                 | 中(解释执行)                 |
| **规则管理** | 无                           | 提供完整规则定义和管理机制   |
| **扩展性**   | 可自定义函数                 | 可自定义规则和动作           |
| **学习曲线** | 较低                         | 中等                         |

### 2.3 选择建议
* 需要复杂计算/表达式处理：选择 Aviator
* 需要完整规则管理系统：选择 Easy Rules
* 两者结合：可以在 Easy Rules 的规则条件中使用 Aviator 来处理复杂表达式,在规则动作中使用Easy-Rules的actions动作。

### 2.4 计划
我们这边的将选择Aviator与Easy-rules结合，采用Aviator作为表达式计算，Easy-Rules作为规则管理。
下面是我的计划安排
1. 第一阶段
* 利用Avitor做成一个规则引擎的工具类，定义规则以及规则的组合，解析规则，执行条件表达式。
2. 第二阶段
* 集成Easy-Rules用于规则管理，aviator作为条件表达式执行则，easy-rules作为规则管理。
3. 第三阶段
* 建立标签系统
* 建立客群系统，通过选择标签系统，配置客群规则，创建规则组，达到组合复用的效果。
* 可视化管理规则组，建立规则后管。

### 2.5 进程
1. 第一阶段已经完成，通过Aviator表达式引擎构建规则以及规则组
2. 第二阶段
* 集成Easy-Rules用于actions，aviator作为条件表达式执行则，easy-rules作为规则管理。


## 3. 数据结构
### 3.1 规则与规则引入
要想设计出规则组，那么就要弄明白规则，以及如何将规则组合起来。
1. 规则
* 规则无非就是定义一些规则，最好就是能用表达式简单来表达出来，也就是定义一些将需要是**实际值** 与 **期望值** 用**运算符**
  连接起来，然后通过运算逻辑把最终的比较的值确认出来。恰好Aviator的表达式就能将 实际值、期望值、运算符
  三者表示出来，并且运用Aviator的表达式引擎jj计算出来，且Aviator有丰富的运算符以及强大的计算能力。
* 综上分析，规则就是一个表达式的定义。
* 规则可以看成是原子的定义

2. 规则组
* 规则组就是一系列规则的组合，各项规则之间的组合用什么关系来桥接呢，很容易让联想到计算机语言中的逻辑关系(AND、OR)
* 规则组中重点是在逻辑关系的定义，然后规则可以看成是规则组的子节点。
所以我们规则组以及规则的样本如下：
![himg](https://chaser-zh-bucket.oss-cn-shenzhen.aliyuncs.com//uPic/ppeTG9.png)
> 考虑到一个父节点，存在多个子节点，构成多叉树，增加了树结构的复杂性，以及子树之间的逻辑关系难以维护，我们采用标准的二叉树表示


### 3.2 数据结构
通过上面的分析，我们的数据结构定义如下：
1. 规则与规则组的抽象
```
public abstract class RuleNode implements Serializable {

    private static final long serialVersionUID = 2945212834894124768L;
    /**
     * 节点id
     */
    protected String id;
    /**
     * 节点名称
     */
    protected String name;
    /**
     * 优先级
     */
    protected int priority;
    /**
     * 是否启用
     */
    protected boolean enabled = true;

    /**
     * 节点描述
     */
    protected  String description;

    /**
     * 节点类型LEAF,COMPOSITE
     */
    protected NodeType nodeType;

    /**
     * 执行规则，返回执行结果
     */
    public abstract RuleResult evaluateWithActions(Facts facts);
}

```

2. 规则的定义
```
public class RuleDefinition extends RuleNode {

    /**
     * 表达式
     */
    private String expression;

    /**
     * 动作
     */
    private List<String> actions;
    @Override
    public RuleResult evaluateWithActions(Facts facts) {
        if (!enabled) {
            return new RuleResult(id, true, "enabled is false");
        }
        boolean pass;
        try {
            pass = Boolean.TRUE.equals(AviatorEvaluator.execute(expression, facts.asMap(), true));
        } catch (Exception e) {
            return new RuleResult(id, false, "表达式执行异常：" + e.getMessage());
        }
        if (pass && actions != null) {
            for (String actionName : actions) {
                Rule action = ActionRegistry.getAction(actionName);
                if (action == null) {
                    return new RuleResult(id, false, "动作未注册：" + actionName);
                }
                try {
                    action.execute(facts);
                } catch (Exception e) {
                    return new RuleResult(id, false, "动作执行异常：" + e.getMessage());
                }
            }
        }
        return new RuleResult(id, pass, pass ? "规则通过，动作执行成功" : "规则未通过");
    }
}

```

3. 规则组的聚合
```
public class RuleGroup extends RuleNode {

    /**
     * 逻辑类型 AND,OR
     */
    private LogicType logic;

    /**
     * 子节点
     */
    private List<RuleNode> children = new ArrayList<>();

    /**
     * 满足条件后的actions
     */
    private List<String> actions = new ArrayList<>();
    @Override
    public RuleResult evaluateWithActions(Facts facts) {
        if (!enabled) {
            return new RuleResult(id, true, "规则组未启用，默认通过");
        }
        
        List<RuleResult> childResults = new ArrayList<>();
        boolean passed;

        List<RuleNode> activeChildren = children.stream()
                .filter(RuleNode::isEnabled)
                .sorted(Comparator.comparingInt(RuleNode::getPriority))
                .collect(Collectors.toList());

        if (logic == LogicType.AND) {
            passed = true;
            for (RuleNode child : activeChildren) {
                RuleResult res = child.evaluateWithActions(facts);
                childResults.add(res);
                if (!res.isPass()) {
                    passed = false;
                    break;
                }
            }
        } else { // OR
            passed = false;
            for (RuleNode child : activeChildren) {
                RuleResult res = child.evaluateWithActions(facts);
                childResults.add(res);
                if (res.isPass()) {
                    passed = true;
                    break;
                }
            }
        }

        if (passed && actions != null) {
            for (String actionName : actions) {
                Rule action = ActionRegistry.getAction(actionName);
                if (action != null) {
                    try {
                        action.execute(facts);
                    } catch (Exception e) {
                        return new RuleResult(id, false, "动作执行异常：" + e.getMessage());
                    }
                } else {
                    return new RuleResult(id, false, "动作未注册：" + actionName);
                }
            }
        }

        return new RuleResult(id, passed,
                passed ? "组合规则通过" : "组合规则未通过",
                childResults);
    }
}

```

* 规则与规则组的抽象，更多的是定义单个规则表达式计算能力以及规则组的聚合结果的计算能力
* 规则组的聚合结果丢给下面子树的规则或者子树规则组去计算，这更加凸显面向对象的编程思想。


## 4 规则组模型
规则组模型配置我们支持多种数据格式，比如yaml、json、propertis、jsonStr
我们以yaml格式为例
```

rules:
  - id: "rule-group-1"
    name: "用户标签组合规则"
    type: COMPOSITE
    logic: AND
    priority: 1
    actions:
      - "SendCouponAction"
    children:
      - id: "r1"
        name: "判断是否在客群A"
        type: LEAF
        expression: "isInCrowd(uid, '200003')"
      - id: "group-1"
        name: "子组合组"
        type: COMPOSITE
        logic: OR
        children:
          - id: "r2"
            name: "余额大于100"
            type: LEAF
            expression: "balance > 100"
          - id: "r3"
            name: "年龄小于30"
            type: LEAF
            expression: "age < 30"
```


## 5. 规则组的解析与校验、执行
1. 规则组的解析、校验、执行统一入口
```
public RuleResult executeFromFile(String fileContent, FileType format, String ruleGroupId, Map<String, Object> input) {
    String cacheKey = "file::" + format + "::" + ruleGroupId;
    RuleNode group = cache.computeIfAbsent(cacheKey, key -> {
        List<RuleNode> groups = RuleParserFactory.getParser(ParserType.convertFromFileType(format).getParserType()).parse(fileContent);
        return findRuleGroupById(groups, ruleGroupId);
    });
    validator.validate(group);
    return executor.executeRuleNode(group, FactsUtils.fromMap(input, true));
}
```
### 5.1 解析
```
public interface RuleParser {


    String getFormate();

    /**
     * 解析规则
     * @param content 规则内容
     * @return 规则节点列表
     */
    List<RuleNode> parse(String content);
}
```
配置文件我们支持yaml、json、properties文件格式，以及jsonStr格式,我们用工厂聚合不同的解析实现类并选择具体的解析动作


### 5.2、校验
```
/**
 * 校验规则组合法性（包括递归校验子节点）
 */
public void validate(RuleGroup group) {
    log.info("begin validate rule group: {}", group.getId());
    if (group == null) {
        throw new RuleEngineException("Rule group is null");
    }

    Set<String> ids = new HashSet<>();
    validateNode(group, ids);
    log.info("end validate rule group: {}", group.getId());
}
```

### 5.3、执行
```

```
规则组的执行交由规则组自己执行，其核心就是借助Aviator表达式引擎，执行通过之后并执行actions，比如发券等操作
```
  public RuleResult executeRuleNode(RuleNode ruleNode, Facts facts) {

      return ruleNode.evaluateWithActions(facts);
  }
```

## 6. 表达式的拓展
* Aviator表达式引擎不仅支持基础的运算符，还能支持自定义拓展的函数。
* Aviator提供了强大的计算拓展能力
* 拓展函数，拓展的钩子就是AbstractFunction

### 6.1 自定义拓展表达式函数
例如我们自定义一个客群的表达式
> 因为需要客群的定义需要借助外部函数，aviator自定义的表达式不满足，所以我们需要自定义一个客群表达式，并且把这个客群表达式函数注册到aviator引擎上。
> 我们的项目是一个非spring项目，需要将AbstractFunction实现类给注册到AviatorEvaluator,我们就需要使用jdk原生的spi机制
#### 1. 定义拓展接口
拓展接口用于关联AbstractFunction以及便于SPI识别AbstractFunction的自定义函数
```
/**
 * 定义接口标识
 */
public interface RuleFunctionMarker {
}
```

#### 2. 自定义拓展表达式函数
```
**
 * 自定义的客群函数
 * 所有自定义函数都实现这个标记接口用于SPI插件机制自动发现并注册
 */

public class CrowdFunction extends AbstractFunction implements RuleFunctionMarker {


    private static final Logger log = LoggerFactory.getLogger(CrowdFunction.class);


    @Override
    public String getName() {
        return "isInCrowd";
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject uidObj, AviatorObject crowdIdObj) {
        Long uid = Long.valueOf(uidObj.getValue(env).toString());
        String crowdId = crowdIdObj.getValue(env).toString();
        // 模拟调用客群服务接口（实际请调用真实服务）
        boolean result = mockCheckCrowd(uid, crowdId);
        log.debug("调用 isInCrowd({}, {}) = {}", uid, crowdId, result);

        return AviatorBoolean.valueOf(result);
    }

    private boolean mockCheckCrowd(Long uid, String crowdId) {
        // TODO: 调用外部系统，如客群服务，查询 uid 是否在指定 crowdId 中
        // 这里只是模拟：uid 为偶数且 crowdId 为 'crowdA' 才返回 true
        return uid % 2 == 0 && "200003".equals(crowdId);
    }

}
```

#### 3. 定义AbstractFunction实现类的SPI文件
我们需要在resource目录下/META-INF.services文件下创建SPI文件
> com.tommy.rulesengine.function.CrowdFunction

#### 4. 注册自定义表达式函数到AviatorEvaluator
```
/**
 * SPI自动发现并注册自定义函数
 */
public class FunctionRegistrar {

    private static final Logger log = LoggerFactory.getLogger(FunctionRegistrar.class);


    public static void registerAll() {
        ServiceLoader<RuleFunctionMarker> loader = ServiceLoader.load(RuleFunctionMarker.class);

        for (RuleFunctionMarker function : loader) {
            if (function instanceof AbstractFunction ) {
                AbstractFunction abstractFunction = (AbstractFunction) function;
                AviatorEvaluator.addFunction(abstractFunction);
                log.info("Registered Aviator function: {}", abstractFunction.getName());
            }
        }
    }
}

```

#### 5、FunctionRegistrar执行
在我们解析、校验、执行规则组之前就需要将自定义函数注册到AviatorExecutor,
类似我们spring容器初始化完成之后init操作一样，因为我们是非spring，所以我们在执行器构造之前就要注册所有钩子函数
我们采用static块
```
    // 注册所有自定义函数
    static {
        FunctionRegistrar.registerAll();
    }
```

### 6.2 接入方自定义函数建议
如果接入方引入这个sdk，并且想自定义函数需要注意点
1. 自定义函数需要同时继承AbstractFunction的call方法并重写，并实现RuleFunctionMarker便于SPI或者spring识别具体实现类。
2. 如果是接入方是非spring，需要编写spi文件


### 7 动作actions拓展
* 动作actions拓展就是在规则组执行通过之后，执行一些业务逻辑，比如发券、发短信等操作
* 动作actions选择用easy-rules，因为easy-rules支持自定义拓展的函数，我们可以在action中调用自定义的函数
* 类似aviator自定义函数，采用spi机制加载

### 7.1 自定义自定义函数拓展接口
拓展接口用于关联AbstractFunction以及便于SPI识别AbstractFunction的自定义函数
#### 1. 自定义actions函数
```
/**
 * 定义接口标识
 */
public interface RuleActionMarker extends Rule {
}
```

#### 2. 自定义actions函数实现
```
public class SendCouponAction implements RuleActionMarker {

    private boolean enabled = true;

    @Override
    public String getName() {
        return "SendCouponAction";
    }

    @Override
    public String getDescription() {
        return "发送优惠券的动作";
    }

    @Override
    public int getPriority() {
        return 1; // 优先级
    }

    @Override
    public boolean evaluate(Facts facts) {
        // 动作本身通常不用条件判断，这里可以返回true表示执行
        return enabled;
    }

    @Override
    public void execute(Facts facts) throws Exception {
        String uid = (String) facts.get("uid");
        // 业务逻辑，比如发优惠券
        System.out.println("发送优惠券给用户: " + uid);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public int compareTo(Rule o) {
        if (o == null) {
            return -1;
        }
        return Integer.compare(this.getPriority(), o.getPriority());
    }
}
```
#### 3. 定义Rule实现类的SPI文件
我们需要在resource目录下/META-INF.services文件下创建SPI文件
文件名为：com.tommy.rulesengine.actions.RuleActionMarker
文件内容为自定义的函数绝对限定名
> com.tommy.rulesengine.function.CrowdFunction

#### 4. 注册自定义表达式函数到注册中心
```
public class ActionRegistry {

    private static final Logger log = LoggerFactory.getLogger(ActionRegistry.class);


    private static final ConcurrentHashMap<String, Rule> ACTIONS = new ConcurrentHashMap<>();

    static {
        ServiceLoader<RuleActionMarker> loader = ServiceLoader.load(RuleActionMarker.class);
        for (RuleActionMarker action : loader) {
            ACTIONS.put(action.getName(), action);
            log.info("Registered SPI action: {}" , action.getName());
        }
    }
    

    public static Rule getAction(String name) {
        return ACTIONS.get(name);
    }
}
```

### 7.2 在运行时加载自定义actions
在运行时加载自定义actions,我们需要在规则组执行之前，将自定义actions注册到ActionRegistry中
```
for (String actionName : actions) {
    Rule action = ActionRegistry.getAction(actionName);
    if (action != null) {
        try {
            action.execute(facts);
        } catch (Exception e) {
            return new RuleResult(id, false, "动作执行异常：" + e.getMessage());
        }
    } else {
        return new RuleResult(id, false, "动作未注册：" + actionName);
    }
}
```

## 8. 接入方式
### 8.1 接入方式
接入方可以通过以下两种方式接入我们的sdk
1. 下载我们的jar包，推送到自己的私服，便可直接使用
下载目录，在项目的release目录对应的版本下，可以下载源码以及jar包
2. 通过引入pom坐标，远程下载我们的第三方jar包
在pom文件下，引入下面代码
```
<repositories>
    <repository>
        <id>github</id>
        <name>GitHub Packages</name>
        <url>https://maven.pkg.github.com/chaserZH/rule-engine</url>
    </repository>
</repositories>

<dependencies>
<dependency>
  <groupId>com.chaserzh.rule</groupId>
  <artifactId>rule-engine</artifactId>
  <version>1.0.3</version>
</dependency>
</dependencies>
```
### 8.2 接入入口
> com.tommy.rulesengine.util.RuleEngineUtil

### 8.3 接入规则配置方式
1. 支持apollo的yaml、propertis、yml、json文件配置rules
2、支持接入方本地的yaml、propertis、yml、json文件配置rules
3、支持接入方自行构造RuleGroup
4、支持接入方传递RuleGroup的json字符串。


## 9 后期规划
1. 已经完成第一阶段计划，集成aviator表达式引擎,并完成规则组定义与管理
2. 已经完成第二阶段计划，集成aviator与easy-rules，规则判断采用aviator,动作actions采用easy-rules
3. 已经在构想如何构建标签、以及规则组配置以及可视化管理
4. 最终的目的就是希望做成规则引擎、供业务使用，减少代码开发以及管理



