package com.example.sampleapp.service;

import com.example.sampleapp.dal.repo.EmployeeRepo;
import com.example.sampleapp.generated.grpc.EmployeeDetails;
import com.example.sampleapp.generated.grpc.EmployeeIdentifier;
import com.google.protobuf.Int32Value;
import com.google.protobuf.StringValue;
import com.example.sampleapp.dal.entity.Employee;
import com.example.sampleapp.exception.CorruptDataException;
import com.example.sampleapp.exception.NotFoundException;
import com.example.sampleapp.util.FakeStreamObserver;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImpITest {
  @Mock private EmployeeRepo employeeRepo;

  @InjectMocks private final EmployeeServiceImpl subject = new EmployeeServiceImpl();

  @Test
  @SuppressWarnings("unchecked")
  void testFindByEmployeeWhenNoEmployeeIDProvided() {
    // arrange
    final EmployeeIdentifier employeeIdentifier = EmployeeIdentifier.newBuilder().build();
    // act & assert
    assertThrows(
        IllegalArgumentException.class,
        () -> subject.findById(employeeIdentifier, Mockito.mock(StreamObserver.class)));
  }

  @Test
  @SuppressWarnings("unchecked")
  void testFindByEmployeeWhenEmployeeNotFound() {
    // arrange
    final EmployeeIdentifier employeeIdentifier =
        EmployeeIdentifier.newBuilder().setEmployeeId(123).build();
    // act & assert
    assertThrows(
        NotFoundException.class,
        () -> subject.findById(employeeIdentifier, Mockito.mock(StreamObserver.class)));
  }

  @Test
  @SuppressWarnings("unchecked")
  void testFindByEmployeeWhenAdditionalEmployeeDataDoNotExists() {
    // arrange
    final Employee employeeEntity =
        Employee.builder().employeeName("Test Employee").employeeId(123).build();
    Mockito.when(employeeRepo.findById(123)).thenReturn(Optional.of(employeeEntity));
    final EmployeeIdentifier employeeIdentifier =
        EmployeeIdentifier.newBuilder().setEmployeeId(123).build();
    // act & assert
    assertThrows(
        CorruptDataException.class,
        () -> subject.findById(employeeIdentifier, Mockito.mock(StreamObserver.class)));
  }

  @Test
  @SuppressWarnings("unchecked")
  void testFindByEmployeeWhenRequiredEmployeeDataIsMissing() {
    // arrange
    final Employee employeeEntity =
        Employee.builder().employeeName("Test Employee").employeeId(123).build();
    Mockito.when(employeeRepo.findById(123)).thenReturn(Optional.of(employeeEntity));
    final EmployeeIdentifier employeeIdentifier =
        EmployeeIdentifier.newBuilder().setEmployeeId(123).build();
    // act & assert
    assertThrows(
        CorruptDataException.class,
        () -> subject.findById(employeeIdentifier, Mockito.mock(StreamObserver.class)));
  }

  @Test
  void testFindByEmployeeWhenEmployeeFoundWithRequiredFields() {
    // arrange
    final Employee employeeEntity =
        Employee.builder().employeeName("Test Employee").employeeId(123).build();
    employeeEntity.getAdditionalEmployeeData().joiningYear(2010);
    Mockito.when(employeeRepo.findById(123)).thenReturn(Optional.of(employeeEntity));
    final FakeStreamObserver<EmployeeDetails> responseObserver = new FakeStreamObserver<>();
    // act
    subject.findById(EmployeeIdentifier.newBuilder().setEmployeeId(123).build(), responseObserver);
    // assert
    final EmployeeDetails expected =
        EmployeeDetails.newBuilder()
            .setEmployeeId(123)
            .setEmployeeName("Test Employee")
            .setJoiningYear(2010)
            .build();
    assertEquals(expected, responseObserver.getResponse(), "Incorrect employee found");
  }

  @Test
  void testFindByEmployeeWhenEmployeeFoundWithAllFields() {
    // arrange
    final Employee employeeEntity =
        Employee.builder().employeeName("Test Employee").employeeId(123).birthYear(1985).build();
    employeeEntity.getAdditionalEmployeeData().joiningYear(2010).aliasName("Whatever");

    Mockito.when(employeeRepo.findById(123)).thenReturn(Optional.of(employeeEntity));
    final FakeStreamObserver<EmployeeDetails> responseObserver = new FakeStreamObserver<>();
    // act
    subject.findById(EmployeeIdentifier.newBuilder().setEmployeeId(123).build(), responseObserver);
    // assert
    final EmployeeDetails expected =
        EmployeeDetails.newBuilder()
            .setEmployeeId(123)
            .setEmployeeName("Test Employee")
            .setBirthYear(Int32Value.of(1985))
            .setJoiningYear(2010)
            .setAliasName(StringValue.of("Whatever"))
            .build();
    assertEquals(expected, responseObserver.getResponse(), "Incorrect employee found");
  }
}
