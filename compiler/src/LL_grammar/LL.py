# 定义终止符与非终止符
class ele:
    def __init__(self, char, type):
        self.char = char
        self.type = type  # 1代表非终止符，0代表终止符

#终止符与非终止符
E = ele('E', 1)
T = ele('T', 1)
F = ele('F', 1)
E_1 = ele('E\'', 1)
T_1 = ele('T\'', 1)
num = ele('num', 0)
add = ele('+', 0)
minus = ele('-', 0)
multi = ele('*', 0)
divis = ele('/', 0)
leftbrackets = ele('(', 0)
rightbrackets = ele(')', 0)
end = ele('$',0)
terminator=[num,add,minus,multi,divis,leftbrackets,rightbrackets]
nonterminator=[E,T,E_1,T_1,F]

#语法推导规则的定义
rule_1 = [E,T,E_1]
rule_2 = [E_1,add,T,E_1]
rule_3 = [E_1,minus,T,E_1]
rule_4 = [E_1]#表示推导到空的情况
rule_5 = [T,F,T_1]
rule_6 = [T_1,multi,F,T_1]
rule_7 = [T_1,divis,F,T_1]
rule_8 = [T_1]
rule_9 = [F,leftbrackets,E,rightbrackets]
rule_10 = [F,num]
rule_all=[rule_1,rule_2,rule_3,rule_4,rule_5,
          rule_6,rule_7,rule_8,rule_9,rule_10]

#计算一个字典中的元素总数
def countNum(dic):
    count = 0
    for key,value in dic.items():
        dic[key] = list(set(value))
        count += len(dic[key])
    return count


#输出语法规则
def printRule(rule):
    print(rule[0].char,end='')
    print("->",end='')
    for index in range(1,len(rule)):
        print(' '+rule[index].char,end='')

#计算First集
def getFirst():
    First={'E':[],'T':[],'F':[],'E\'':[],'T\'':[]}
    #先将所有直接终止符前缀加入
    for rule in rule_all:
        if (len(rule)!=1 and rule[1].type==0):
            First[rule[0].char].append(rule[1])
    #接着一直迭代，直到集合不再变化
    count=countNum(First)#先计算First集合种元素总数
    unchange=0
    #迭代，每次迭代完成后比较集合种总数是否有变化
    while unchange==0:
        for rule in rule_all:
            if (len(rule) != 1 and rule[1].type == 1):
                First[rule[0].char].extend(First[rule[1].char])
                First[rule[0].char]=list(set(First[rule[0].char]))
        count_temp=count
        count = countNum(First)
        if (count_temp==count):
            unchange=1
    #集合不再变化后即计算完成
    return First

#判断First集合中是否有空集
def isNullInFirst(terminator):
    judge=0
    for rule in rule_all:
        if(rule[0].char==terminator.char and len(rule)==1):
            judge=1
    return judge



#计算Follow集
def getFollow():
    Follow = {'E': [], 'T': [], 'F': [], 'E\'': [], 'T\'': []}
    #先将直接出现在后面的非终止符的Fist集合与终止符加入
    for rule in rule_all:
        if(len(rule)!=1):
            for index in range(1, len(rule) - 1):
                if (rule[index].type == 1 and rule[index + 1].type == 0):
                    Follow[rule[index].char].append(rule[index + 1])
                    Follow[rule[index].char] = list(set(Follow[rule[index].char]))
                elif (rule[index].type == 1 and rule[index + 1].type == 1):
                    Follow[rule[index].char].extend(FirstSet[rule[index + 1].char])
                    Follow[rule[index].char] = list(set(Follow[rule[index].char]))
    #接着将推导式左侧非终止符的Follow集合加入
    for rule in rule_all:
        if(len(rule)!=1):
            for index in range(1, len(rule) - 1):
                if (rule[index].type == 1 and rule[index + 1].type == 1 and isNullInFirst(rule[index + 1])):
                    Follow[rule[index].char].extend(Follow[rule[0].char])
                    Follow[rule[index].char] = list(set(Follow[rule[index].char]))
            if (rule[len(rule) - 1].type == 1):
                Follow[rule[len(rule) - 1].char].extend(Follow[rule[0].char])
                Follow[rule[len(rule) - 1].char] = list(set(Follow[rule[len(rule) - 1].char]))
    return Follow

#构建预测分析表
def createPredictionTabel():
    PredictionTabel = {'E': {}, 'T': {}, 'F': {}, 'E\'': {}, 'T\'': {}}
    for rule in rule_all:
        # 先处理First集的情况
        if(len(rule)!=1):
            #推导式右侧第一个字符是终止符
            if(rule[1].type==0):
                PredictionTabel[rule[0].char][rule[1].char]=rule
            #推导式右侧第一个字符是非终止符
            if(rule[1].type==1):
                for ele in FirstSet[rule[1].char]:
                    PredictionTabel[rule[0].char][ele.char] = rule
        #接着处理Follow集的情况
        else:
            for ele in FollowSet[rule[0].char]:
                PredictionTabel[rule[0].char][ele.char] = rule
    #最后再处理推导至空的情况
            PredictionTabel[rule[0].char]['$'] = rule
    return PredictionTabel


def error():
    print("分析出错")

FirstSet=getFirst()
FollowSet=getFollow()
PredictionTable=createPredictionTabel()

#进行分析过程
def analysis(str):
    #定义输入栈
    inputStack=[]
    for index in range(len(str)):
        if(str[index].isdigit()):
            if(index == (len(str)-1) or (1 - str[index+1].isdigit())):
                inputStack.append('num')#对于数字的识别并进行特殊处理
        else:
            inputStack.append(str[index])
    inputStack.append('$')
    #定义分析栈
    analysisStack=[end,E]
    #开始进行分析
    while (len(analysisStack)!=1):
        if(len(inputStack)==1 and len(analysisStack)==1):#当输入栈为空时，结束分析
            break
        #进行分析，分别考虑分析栈栈尾为终止符与非终止符的情况
        if(analysisStack[len(analysisStack)-1].type==0):
            if(analysisStack.pop().char!=inputStack.pop(0)):
                error()
                break
        else:
            if(inputStack[0] in PredictionTable[analysisStack[len(analysisStack)-1].char].keys()):
                temp=PredictionTable[analysisStack.pop().char][inputStack[0]]
                for index in range(1,len(temp)):
                    analysisStack.append(temp[len(temp)-index])
            else:
                error()
                break
    if(len(inputStack)!=1):
        error()
    else:
        print("分析成功")


analysis("()1+(1*(21))")
