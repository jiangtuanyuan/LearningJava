package cn.ccsu.learning.ui.main.fragment.test.bean;

public class TestDetailsBean {
    /**
     * exId : 1
     * typeId : 1
     * exCont : Java程序中的起始类名称必须与存放该类的文件名相同。（）
     * exAnswer : True
     * exKind : 判断
     * exGrade : 5
     * choiceA :
     * choiceB :
     * choiceC :
     * choiceD :
     * id : null
     * pid : null
     * title : null
     */
    private String exId;
    private String typeId;
    private String exCont;
    private String exAnswer;
    private String exKind;
    private String exGrade;
    private String choiceA;
    private String choiceB;
    private String choiceC;
    private String choiceD;
    private int id;
    private int pid;
    private String title;

    private String checkString = "N";//选择的答案  N是没有选择 A B C D true false 只有这四个值
    private boolean isCommit=false;//是否交卷

    public boolean isCommit() {
        return isCommit;
    }

    public void setCommit(boolean commit) {
        isCommit = commit;
    }

    public String getCheckString() {
        return checkString;
    }

    public void setCheckString(String checkString) {
        this.checkString = checkString;
    }

    public String getExId() {
        return exId;
    }

    public void setExId(String exId) {
        this.exId = exId;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getExCont() {
        return exCont;
    }

    public void setExCont(String exCont) {
        this.exCont = exCont;
    }

    public String getExAnswer() {
        return exAnswer;
    }

    public void setExAnswer(String exAnswer) {
        this.exAnswer = exAnswer;
    }

    public String getExKind() {
        return exKind;
    }

    public void setExKind(String exKind) {
        this.exKind = exKind;
    }

    public String getExGrade() {
        return exGrade;
    }

    public void setExGrade(String exGrade) {
        this.exGrade = exGrade;
    }

    public String getChoiceA() {
        return choiceA;
    }

    public void setChoiceA(String choiceA) {
        this.choiceA = choiceA;
    }

    public String getChoiceB() {
        return choiceB;
    }

    public void setChoiceB(String choiceB) {
        this.choiceB = choiceB;
    }

    public String getChoiceC() {
        return choiceC;
    }

    public void setChoiceC(String choiceC) {
        this.choiceC = choiceC;
    }

    public String getChoiceD() {
        return choiceD;
    }

    public void setChoiceD(String choiceD) {
        this.choiceD = choiceD;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
