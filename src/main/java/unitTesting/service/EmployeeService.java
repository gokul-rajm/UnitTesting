package unitTesting.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import unitTesting.entity.Employee;
import unitTesting.repository.EmployeeRepository;

@Service
public class EmployeeService {
    
	@Autowired
    private EmployeeRepository employeeRepository;

	public ResponseEntity<Employee> saveEmployee(Employee employee) {
	    Employee savedEmployee = employeeRepository.save(employee);
	    return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
	}

	public Employee getEmployeeById(Long id) {
	    return employeeRepository.findById(id)
	        .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID " + id));
	}

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
    
    public Employee updateEmployee(Long id, Employee employee) {
        return employeeRepository.findById(id)
                .map(existingEmployee -> {
                    existingEmployee.setName(employee.getName());
                    existingEmployee.setEmail(employee.getEmail());
                    return employeeRepository.save(existingEmployee);
                })
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }
}

