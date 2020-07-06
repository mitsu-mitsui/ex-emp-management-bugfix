package jp.co.sample.emp_management.controller;

import java.util.List;

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
	 * 従業員一覧画面の表示．
	 * 
	 * @param model      ページ数情報を格納するリクエストスコープ
	 * @param nowPageNum 遷移先のページ番号
	 * @param name       検索時に格納される名前
	 * @return 初回・検索結果hitなし：従業員情報一覧表示/検索時：検索結果一覧表示
	 */
	@RequestMapping("/showList")
	public String showPagingList(Model model, String name, Integer nowPageNum) {
		if (name == null) {// 最初
			name = "";
		}
		if (nowPageNum == null) {// 最初
			nowPageNum = 0;
		}
		if (employeeService.getSerchHitNum(name) == 0) {// nohit
			model.addAttribute("employeeListisEmpty", "1件も該当しませんでした。");
		}

		List<Employee> employeePagingList = employeeService.showPagingListByName(name, nowPageNum);
		model.addAttribute("employeePagingList", employeePagingList);

		model.addAttribute("name", name);
		model.addAttribute("nowPageNum", nowPageNum);
		model.addAttribute("pageNumList", employeeService.getPageNumList(name));

		System.out.println(employeeService.getPageNumList(name));

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
