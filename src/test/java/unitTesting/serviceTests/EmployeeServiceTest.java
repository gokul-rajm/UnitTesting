package unitTesting.serviceTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import unitTesting.entity.Employee;
import unitTesting.repository.EmployeeRepository;
import unitTesting.service.EmployeeNotFoundException;
import unitTesting.service.EmployeeService;

@SpringBootTest
public class EmployeeServiceTest {

    @Autowired
    private EmployeeService employeeService;

    @MockBean
    private EmployeeRepository employeeRepository;

    @Test
    public void saveEmployeeTest() {
        // Mock data
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("gokul");
        employee.setEmail("gokul@gmail.com");

        // Mock repository
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        // Call service method
        ResponseEntity<Employee> response = employeeService.saveEmployee(employee);

        // Verify repository method called with correct argument
        ArgumentCaptor<Employee> argumentCaptor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(argumentCaptor.capture());
        assertEquals(employee.getName(), argumentCaptor.getValue().getName());

        // Verify response status and data
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(employee, response.getBody());
    }
    
    @Test
    public void testGetEmployeeById() {
        // Mock data
        long id = 1L;
        String name = "John";
        String email = "john.doe@example.com";
        Employee employee = new Employee(id, name, email);

        // Mock repository
        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));

        // Call service method
        Employee result = employeeService.getEmployeeById(id);

        // Assert results
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(name, result.getName());
        assertEquals(email, result.getEmail());
    }
    
    @Test
    public void testGetAllEmployees() {
        // Arrange
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee(1L, "gokul", "gokul@mail.com"));
        employees.add(new Employee(2L, "raj", "raj@mail.com"));
        when(employeeRepository.findAll()).thenReturn(employees);

        // Act
        List<Employee> result = employeeService.getAllEmployees();

        // Assert
        assertEquals(2, result.size());
        assertEquals("gokul", result.get(0).getName());
        assertEquals("raj@mail.com", result.get(1).getEmail());
    }
    
    @Test
    public void testUpdateEmployee() {
        Employee existingEmployee = new Employee(1L, "gokul", "gokul@mail.com");
        Employee updatedEmployee = new Employee(1L, "raj", "raj@mail.com");
        Mockito.when(employeeRepository.findById(1L)).thenReturn(Optional.of(existingEmployee));
        Mockito.when(employeeRepository.save(existingEmployee)).thenReturn(updatedEmployee);
        Employee result = employeeService.updateEmployee(1L, updatedEmployee);
        Mockito.verify(employeeRepository).findById(1L);
        Mockito.verify(employeeRepository).save(existingEmployee);
        Assert.assertEquals(updatedEmployee, result);
    }
    
    @Test
    void testUpdateNonExistingEmployee() {
        // create a new employee to be updated
        Employee employee = new Employee(1L, "gokul","gokul@mail.com");

        // mock the employee repository to return an empty optional
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        // call the updateEmployee method and assert that it throws an EmployeeNotFoundException
        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.updateEmployee(1L, employee);
        });

        // check the exception message
        assertEquals("Employee not found with id: 1", exception.getMessage());
    }

    @Test
    public void testDeleteExistingEmployee() {
        Long id = 1L;
        when(employeeRepository.existsById(id)).thenReturn(true);
        employeeService.deleteEmployee(id);
        verify(employeeRepository, times(1)).deleteById(id);
    }
}
