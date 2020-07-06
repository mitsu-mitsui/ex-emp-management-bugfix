package jp.co.sample.emp_management.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.sample.emp_management.domain.Employee;
import jp.co.sample.emp_management.repository.EmployeeRepository;

/**
 * 従業員情報を操作するサービス.
 * 
 * @author igamasayuki
 *
 */
@Service
@Transactional
public class EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;

	/**
	 * 1ページ分の従業員情報を取得する．
	 * 
	 * @param name    検索名
	 * @param pageNum 表示するページの番号
	 * @return ページ番号に対して表示する従業員情報
	 */
	public List<Employee> showPagingListByName(String name, int pageNum) {

		List<Employee> list = employeeRepository.findLikeNameByPageNum(name, pageNum);

		return list;
	}

	/**
	 * 検索hit数を取得．
	 * 
	 * @param name 検索名
	 * @return 検索hit数
	 */
	public int getSerchHitNum(String name) {
		return employeeRepository.findLikeName(name);
	}

	/**
	 * ページ数のリストを取得する．
	 * 
	 * @param name 検索名
	 * @return ページ数が格納されたリスト
	 */
	public List<Integer> getPageNumList(String name) {
		int size = employeeRepository.findLikeName(name);

		int maxPage = (size / 10) + 1;

		List<Integer> pageNumList = new ArrayList<>();
		for (int i = 1; i <= maxPage; i++) {
			pageNumList.add(i);
		}

		return pageNumList;
	}

	/**
	 * 従業員情報を取得します.
	 * 
	 * @param id ID
	 * @return 従業員情報
	 * @throws 検索されない場合は例外が発生します
	 */
	public Employee showDetail(Integer id) {
		Employee employee = employeeRepository.load(id);
		return employee;
	}

	/**
	 * 従業員情報を更新します.
	 * 
	 * @param employee 更新した従業員情報
	 */
	public void update(Employee employee) {
		employeeRepository.update(employee);
	}

}
