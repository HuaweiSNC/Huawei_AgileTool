package com.huawei.agilete.northinterface.dao;

import java.util.List;

import com.huawei.agilete.base.action.DBAction;
import com.huawei.agilete.northinterface.bean.OTRegister;
import com.huawei.agilete.northinterface.bean.User;
import com.huawei.networkos.ops.response.RetRpc;

public final class OTRegistDao {

	private static OTRegistDao single = null;
	private DBAction db = new DBAction();

	private OTRegistDao() {

	}

	public synchronized static OTRegistDao getInstance() {
		if (single == null) {
			single = new OTRegistDao();
		}
		return single;
	}

	// 添加注册信息

	public RetRpc regist(String content) {
		RetRpc result = new RetRpc();
		OTRegister register = new OTRegister(content);
		boolean isExit = isUserExit(register.getUserName());
		if (isExit) {
			result.setStatusCode(200);
			result.setContent("<regist>nameExit</regist>");
			return result;
		}
		boolean flag = db.insert("user", "user", register.getUserId(),
				register.getRegistInfo());
		if (flag) {
			result.setStatusCode(200);
			result.setContent("<regist>success</regist>");
		} else {
			result.setStatusCode(200);
			result.setContent("<regist>fail</regist>");
		}
		return result;
	}

	// 获得所有用户信息(包括密码，后台使用)

	public String getAllUserInfoToJava() {
		StringBuffer inFo = new StringBuffer();
		inFo.append("<users>");
		List<String[]> list = db.getAll("user", "user");
		int size = list.size();
		if (null != list && size > 0) {
			for (int i = 0; i < size; i++) {
				String content = list.get(i)[1];
				inFo.append(content);
			}
		}
		inFo.append("</users>");
		return inFo.toString();
	}

	// 删除用户信息

	public boolean deleteUserInfo(String userId) {
		boolean flag = false;
		flag = db.delete("user", "user", userId);
		return flag;
	}

	// 删除所有用户信息

	public boolean deleteAllUserInfo() {
		boolean flag = false;
		List<String[]> list = db.getAll("user", "user");
		int size = list.size();
		if (null != list && size > 0) {
			for (int i = 0; i < size; i++) {
				String userId = list.get(i)[0];
				flag = db.delete("user", "user", userId);
			}
		}
		return flag;
	}

	// 获得所有用户信息(不包括密码，前台展示用户列表使用)

	public String getAllUserInfoToFlex() {
		StringBuffer inFo = new StringBuffer();
		inFo.append("<users>");
		List<String[]> list = db.getAll("user", "user");
		int size = list.size();
		if (null != list && size > 0) {
			for (int i = 0; i < size; i++) {
				String content = list.get(i)[1];
				User user = OTRegister.parseContentToUser(content);
				if (null != user) {
					StringBuffer result = new StringBuffer();
					result.append("<user>");
					result.append("<userId>").append(user.getUserId())
							.append("</userId>");
					if (null != user.getUserName()) {
						result.append("<userName>").append(user.getUserName())
								.append("</userName>");
					} else {
						result.append("<userName>").append("</userName>");
					}
					if (null != user.getRegistTime()) {
						result.append("<registTime>")
								.append(user.getRegistTime())
								.append("</registTime>");
					} else {
						result.append("<registTime>").append("</registTime>");
					}
					if (null != user.getDesc()) {
						result.append("<desc>").append(user.getDesc())
								.append("</desc>");
					} else {
						result.append("<desc>").append("</desc>");
					}
					result.append("</user>");
					inFo.append(result.toString());
				}
			}
		}
		inFo.append("</users>");
		return inFo.toString();
	}

	public RetRpc del(String apiPath) {
		RetRpc result = new RetRpc();

		return result;
	}

	// 判断用户名是否存在,true为存在，false为不存在 name为空时，表示存在
	public boolean isUserExit(String name) {
		boolean flag = false;
		if (null != name && !"".equals(name)) {
			List<String[]> list = db.getAll("user", "user");
			for (int i = 0; i < list.size(); i++) {
				String content = list.get(i)[1];
				OTRegister register = new OTRegister(content);
				if (name.equalsIgnoreCase(register.getUserName())) {
					flag = true;
					break;
				}
			}
		} else {
			flag = true;
		}
		return flag;
	}

	public static void main(String[] args) {
		String content = "<user><userId>00002</userId><userName>wzw123</userName><userPasswd>123456</userPasswd><registTime>123</registTime><desc>好人一个！</desc></user>";
		RetRpc result = null;
		OTRegistDao dao = new OTRegistDao();
		result = dao.regist(content);

		// System.out.println("add:" + result.getStatusCode() + "%%%%%"
		// + result.getContent());

		// //System.out.println(dao.deleteAllUserInfo());

		// System.out.println(dao.getAllUserInfoToJava());
		// System.out.println(dao.getAllUserInfoToFlex());
	}

}
