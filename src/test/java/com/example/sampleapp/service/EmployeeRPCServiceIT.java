package com.example.sampleapp.service;

import com.example.sampleapp.exception.CorruptDataException;
import com.google.protobuf.Int32Value;
import com.google.protobuf.StringValue;
import com.example.sampleapp.SampleAppApplicationTests;
import com.example.sampleapp.dal.entity.AdditionalEmployeeData;
import com.example.sampleapp.dal.entity.Employee;
import com.example.sampleapp.dal.repo.EmployeeRepo;
import com.example.sampleapp.generated.grpc.EmployeeDetails;
import com.example.sampleapp.generated.grpc.EmployeeIdentifier;
import com.example.sampleapp.generated.grpc.EmployeeServiceGrpc;
import io.grpc.StatusRuntimeException;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {"grpc.port=0"})
@SuppressWarnings({"PMD.JUnitTestContainsTooManyAsserts", "ResultOfMethodCallIgnored"})
class EmployeeRPCServiceIT extends SampleAppApplicationTests {
  @Autowired private EmployeeRepo employeeRepo;
  private EmployeeServiceGrpc.EmployeeServiceBlockingStub blockingStub;

  @Override
  protected void beforeAll() {
    blockingStub = EmployeeServiceGrpc.newBlockingStub(channel);
    employeeRepo.deleteAll();
  }

  @Override
  protected void afterAll() {
    employeeRepo.deleteAll();
  }

  @Test
  void testFindByEmployeeWhenNoEmployeeIDProvided() {
    // arrange
    final EmployeeIdentifier employeeIdentifier = EmployeeIdentifier.newBuilder().build();
    // act & assert
    assertEquals(
        "INVALID_ARGUMENT: EmployeeId is required",
        assertThrows(StatusRuntimeException.class, () -> blockingStub.findById(employeeIdentifier))
            .getMessage(),
        "Expected INVALID_ARGUMENT exception");
  }

  @Test
  void testFindByEmployeeWhenEmployeeIdtFound() {
    // arrange
    final EmployeeIdentifier employeeIdentifier =
        EmployeeIdentifier.newBuilder().setEmployeeId(123).build();
    // act & assert
    assertEquals(
        "NOT_FOUND: Employee not found",
        assertThrows(StatusRuntimeException.class, () -> blockingStub.findById(employeeIdentifier))
            .getMessage(),
        "Expected NOT_FOUND exception");
  }

  @Test
  void testFindByEmployeeWhenAdditionalEmployeeDataDoNotExists() {
    // arrange
    final Employee employeeEntity = Employee.builder().employeeName("Test Employee").build();
    employeeRepo.save(employeeEntity);
    final EmployeeIdentifier employeeIdentifier =
        EmployeeIdentifier.newBuilder().setEmployeeId(employeeEntity.getEmployeeId()).build();
    // act & assert
    assertEquals(
        "DATA_LOSS: Employee data is corrupt",
        assertThrows(StatusRuntimeException.class, () -> blockingStub.findById(employeeIdentifier))
            .getMessage(),
        "Expected DATA_LOST exception");
  }

  @Test
  void testFindByEmployeeWhenRequiredEmployeeDataIsMissing() {
    // arrange
    final Employee employeeEntity = Employee.builder().employeeName("Test Employee").build();
    employeeRepo.save(employeeEntity);
    final EmployeeIdentifier employeeIdentifier =
        EmployeeIdentifier.newBuilder().setEmployeeId(employeeEntity.getEmployeeId()).build();
    // act & assert
    assertEquals(
        "DATA_LOSS: Employee data is corrupt",
        assertThrows(StatusRuntimeException.class, () -> blockingStub.findById(employeeIdentifier))
            .getMessage(),
        "Expected DATA_LOST exception");
  }

  @Test
  void testFindByEmployeeWhenUnexpectedErrorOccurred() {
    // arrange
    // critical field EmployeeName is missing
    final Employee employeeEntity = Employee.builder().build();
    employeeRepo.save(employeeEntity);
    final EmployeeIdentifier employeeIdentifier =
        EmployeeIdentifier.newBuilder().setEmployeeId(employeeEntity.getEmployeeId()).build();
    // act & assert
    assertTrue(
        assertThrows(StatusRuntimeException.class, () -> blockingStub.findById(employeeIdentifier))
            .getMessage()
            .matches("^UNKNOWN"),
        "Expected UNKNOWN exception");
  }

  @Test
  void testFindByEmployeeWhenEmployeeFoundWithRequiredFields() {
    // arrange
    final Employee employeeEntity = Employee.builder().employeeName("Test Employee").build();
    employeeEntity.getAdditionalEmployeeData().joiningYear(2010);
    employeeRepo.save(employeeEntity);
    // act
    final EmployeeDetails actual =
        blockingStub.findById(
            EmployeeIdentifier.newBuilder().setEmployeeId(employeeEntity.getEmployeeId()).build());
    // assert
    final EmployeeDetails expected = transform(employeeEntity);
    assertEquals(expected, actual, "Incorrect employee found");
  }

  @Test
  void testFindByEmployeeWhenEmployeeFoundWithAllFields() {
    // arrange
    final Employee employeeEntity =
        Employee.builder().employeeName("Test Employee").birthYear(1985).build();
    employeeEntity.getAdditionalEmployeeData().joiningYear(2010).aliasName("Whatever");

    employeeRepo.save(employeeEntity);
    // act
    final EmployeeDetails actual =
        blockingStub.findById(
            EmployeeIdentifier.newBuilder().setEmployeeId(employeeEntity.getEmployeeId()).build());
    // assert
    final EmployeeDetails expected = transform(employeeEntity);
    assertEquals(expected, actual, "Incorrect employee found");
  }

  private EmployeeDetails transform(final Employee employee) {
    final EmployeeDetails.Builder employeeDetailsBuilder =
        EmployeeDetails.newBuilder()
            .setEmployeeId(employee.getEmployeeId())
            .setEmployeeName(employee.getEmployeeName());

    if (employee.getBirthYear() != null) {
      employeeDetailsBuilder.setBirthYear(Int32Value.of(employee.getBirthYear()));
    }

    final AdditionalEmployeeData obj = employee.getAdditionalEmployeeData();
    if (ObjectUtils.anyNull(obj.joiningYear())) {
      throw new CorruptDataException("Employee data is corrupt");
    }
    employeeDetailsBuilder.setJoiningYear(obj.joiningYear());

    if (obj.aliasName() != null) {
      employeeDetailsBuilder.setAliasName(StringValue.of(obj.aliasName()));
    }

    return employeeDetailsBuilder.build();
  }
}
