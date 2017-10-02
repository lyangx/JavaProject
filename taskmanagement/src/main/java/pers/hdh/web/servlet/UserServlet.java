package pers.hdh.web.servlet;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.dbutils.DbUtils;
import pers.hdh.beans.User;
import pers.hdh.service.UserService;
import pers.hdh.service.impl.UserServiceImpl;
import pers.hdh.utils.BeanFactory;
import pers.hdh.utils.UUIDUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

//@WebServlet(urlPatterns = {"/user"})
public class UserServlet extends BaseServlet {

    /**
     * 跳转到登录页面
     * @param request
     * @param response
     * @return
     */
    public String loginUI(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return "jsp/index.jsp";
    }

    /**
     * 用户登录
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String login(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String stuid = request.getParameter("stuid");
        String password = request.getParameter("password");

        User user = null;
        try {
            UserService service = (UserService) BeanFactory.getBean("UserService");
            user = service.getByStuidAndPWD(stuid, password);
        } catch (SQLException e){
            logger.error("user表查询记录失败");
            // 登录失败，跳转回到登录页面
            return "/";
        }

        if (user == null) {
            request.setAttribute("msg", "不存在的账户或密码错误");
            return "/";
        } else {
            request.getSession().setAttribute("user", user);
            // 登录成功，跳转到主页面——任务列表，同时查询数据库，展示所有任务
            response.sendRedirect(request.getContextPath() + "/task?method=getTasks&currPage=1&category=&desc=&state=");
            return null;
        }
    }

    /**
     * 跳转到注册页面
     * @param request
     * @param response
     * @return
     */
    public String registUI(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return "jsp/regist.jsp";
    }

    /**
     * 用户注册，添加一条记录到user表中
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String add(HttpServletRequest request, HttpServletResponse response) throws Exception {
        User user = new User();
        // 获取表单数据，封装结果集
        BeanUtils.populate(user, request.getParameterMap());
        user.setUid(UUIDUtils.setId());

        try {
            UserService service = (UserService) BeanFactory.getBean("UserService");
            service.add(user);
        } catch (SQLException e){
            e.printStackTrace();
            logger.error("user表添加记录失败");
            // 重定向，提示注册失败
            response.sendRedirect(request.getContextPath() + "/jsp/registError.jsp");
            return null;
        }
        // 重定向，提示注册成功
        response.sendRedirect(request.getContextPath() + "/jsp/registSuccess.jsp");
        return null;
    }

    /**
     * 退出用户
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 销毁session
        request.getSession().invalidate();
        // 重定向回到登录页面
        response.sendRedirect(request.getContextPath());
        return null;
    }

    /**
     * 修改用户信息
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String update(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 封装数据
        User user = new User();
        BeanUtils.populate(user, request.getParameterMap());

        // 调用service处理业务逻辑
        try {
            UserService service = (UserService) BeanFactory.getBean("UserService");
            service.update(user);
        } catch (SQLException e) {
            logger.error("user表修改记录失败");
            throw e;
        }

        // 将新的user bean对象放入域中，更新数据
        request.getSession().setAttribute("user", user);
        response.sendRedirect(request.getContextPath() + "/jsp/user_info.jsp");
        return null;
    }

}
