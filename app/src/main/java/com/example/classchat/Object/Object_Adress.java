package com.example.classchat.Object;

public class Object_Adress {
    //收货人
    private String name;
    //电话
    private String phone;
    //    //大学
//    private String university;
//    //校区
//    private String partition;
    //详细地址
    private String detail;
    //是否为默认地址
    private Boolean defaut = true;

    public Object_Adress(){}

    public Object_Adress(String name, String phone, String detail, Boolean defaut) {
        this.name = name;
        this.phone = phone;
        this.detail = detail;
        this.defaut = defaut;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

//    public String getUniversity() {
//        return university;
//    }
//
//    public void setUniversity(String university) {
//        this.university = university;
//    }
//
//    public String getPartition() {
//        return partition;
//    }
//
//    public void setPartition(String partition) {
//        this.partition = partition;
//    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Boolean getDefaut() {
        return defaut;
    }

    public void setDefaut(Boolean defaut) {
        this.defaut = defaut;
    }

    public String totalAddrss(){
        return "华南理工大学 南校区 "+detail;
    }


}
