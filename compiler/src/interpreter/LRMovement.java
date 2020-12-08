package interpreter;
//通用的数据结构，表示下一个动作的类型和参数
//具体而言：
//对于reduce，表示使用的产生式
//对于goto，表示对应的非终结符序号
public class LRMovement {
    public LRAction action;
    public int parameter;

    public LRMovement(LRAction action,int parameter){
        this.action=action;
        this.parameter=parameter;
    }

    @Override
    public int hashCode() {
        return action.hashCode()+parameter;
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode()==obj.hashCode();
    }
}
