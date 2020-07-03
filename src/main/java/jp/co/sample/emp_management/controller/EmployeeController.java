package jp.co.sample.emp_management.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import jp.co.sample.emp_management.domain.Employee;
import jp.co.sample.emp_management.form.UpdateEmployeeForm;
import jp.co.sample.emp_management.service.EmployeeService;

/**
 * 従業員情報を操作するコントローラー.
 * 
 * @author igamasayuki
 *
 */
@Controller
@RequestMapping("/employee")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private HttpSession session;

	private String name;

	/**
	 * 使用するフォームオブジェクトをリクエストスコープに格納する.
	 * 
	 * @return フォーム
	 */
	@ModelAttribute
	public UpdateEmployeeForm setUpForm() {
		return new UpdateEmployeeForm();
	}

	/////////////////////////////////////////////////////
	// ユースケース：従業員一覧を表示する
	/////////////////////////////////////////////////////
	/**
	 * 従業員一覧画面を出力します.
	 * 
	 * @param model モデル
	 * @return 従業員一覧画面
	 */
	// @RequestMapping("/showList")
	public String showList(Model model, String name) {
		List<Employee> employeeList;
		if (name == null) {// 初期・空
			employeeList = employeeService.showList();
		} else {// 検索時
			employeeList = employeeService.showListByName(name);
			if (employeeList.isEmpty()) {// リスト空
				employeeList = employeeService.showList();
				model.addAttribute("employeeListisEmpty", "1件もありませんでした");
			}
		}
		model.addAttribute("employeeList", employeeList);

		return "employee/list";
	}

	// pagingを実装する場所
	// @RequestMapping("/showPagingList")
	/**
	 * 従業員一覧画面の表示．
	 * 
	 * @param model      ページ数情報を格納するリクエストスコープ
	 * @param nowPageNum 遷移先のページ番号
	 * @param name       検索時に格納される名前
	 * @return 初回・検索結果hitなし：従業員情報一覧表示/検索時：検索結果一覧表示
	 */
	@RequestMapping("/showList")
	public String showPagingList(Model model, String nowPageNum, String name) {
		List<Employee> employeePagingList;

		if (nowPageNum == null && name == null) {// 初回
			employeePagingList = employeeService.showPagingList(0);
			this.name = name;
			session.setAttribute("name", name);
			session.setAttribute("maxpagesize", employeeService.getMaxPageNum());
		} else {
			if (name == null || "null".equals(name)) {// 検索なし
				employeePagingList = employeeService.showPagingList(Integer.parseInt(nowPageNum));
				session.setAttribute("name", name);
			} else {
				if (!name.equals(this.name)) {
					session.setAttribute("name", name);
					session.setAttribute("maxpagesize", employeeService.getMaxPageNum(name));
				}
				this.name = name;

				if (employeeService.showListByName(name).isEmpty()) {// 1件もない
					employeePagingList = new ArrayList<Employee>();
					model.addAttribute("employeeListisEmpty", "該当の従業員はいませんでした");
				} else {
					try {
						employeePagingList = employeeService.showPagingListByName(name, Integer.parseInt(nowPageNum));
					}catch (Exception e) {
						employeePagingList = employeeService.showPagingListByName(name,0);
					}
				}
				session.setAttribute("name", name);
			}
		}

		model.addAttribute("employeePagingList", employeePagingList);
		model.addAttribute("firstPageNum", 0);

		if (nowPageNum == null) {// 初期
			model.addAttribute("nowPageNum", 0);
		} else {
			model.addAttribute("nowPageNum", nowPageNum);
		}

		return "employee/list";
	}

	/////////////////////////////////////////////////////
	// ユースケース：従業員詳細を表示する
	/////////////////////////////////////////////////////
	/**
	 * 従業員詳細画面を出力します.
	 * 
	 * @param id    リクエストパラメータで送られてくる従業員ID
	 * @param model モデル
	 * @return 従業員詳細画面
	 */
	@RequestMapping("/showDetail")
	public String showDetail(String id, Model model) {
		Employee employee = employeeService.showDetail(Integer.parseInt(id));
		model.addAttribute("employee", employee);
		return "employee/detail";
	}

	/////////////////////////////////////////////////////
	// ユースケース：従業員詳細を更新する
	/////////////////////////////////////////////////////
	/**
	 * 従業員詳細(ここでは扶養人数のみ)を更新します.
	 * 
	 * @param form 従業員情報用フォーム
	 * @return 従業員一覧画面へリダクレクト
	 */
	@RequestMapping("/update")
	public String update(@Validated UpdateEmployeeForm form, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return showDetail(form.getId(), model);
		}
		Employee employee = new Employee();
		employee.setId(form.getIntId());
		employee.setDependentsCount(form.getIntDependentsCount());
		employeeService.update(employee);
		return "redirect:/employee/showList";
	}

}
