package com.example.sampleapp.service;

import com.example.sampleapp.dal.entity.AdditionalEmployeeData;
import com.example.sampleapp.dal.entity.Employee;
import com.example.sampleapp.dal.repo.EmployeeRepo;
import com.example.sampleapp.exception.CorruptDataException;
import com.example.sampleapp.exception.NotFoundException;
import com.example.sampleapp.generated.grpc.EmployeeDetails;
import com.example.sampleapp.generated.grpc.EmployeeIdentifier;
import com.example.sampleapp.generated.grpc.EmployeeServiceGrpc;
import com.example.sampleapp.interceptor.OtelTraceServerInterceptor;
import com.example.sampleapp.utils.AppConstants;
import com.google.protobuf.Int32Value;
import com.google.protobuf.StringValue;
import io.grpc.stub.StreamObserver;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.lognet.springboot.grpc.GRpcService;

@GRpcService(interceptors = OtelTraceServerInterceptor.class)
@RequiredArgsConstructor
public class EmployeeServiceImpl extends EmployeeServiceGrpc.EmployeeServiceImplBase {

  private final EmployeeRepo employeeRepo;

  @Override
  public void findById(
      final EmployeeIdentifier request, final StreamObserver<EmployeeDetails> responseObserver) {
    if (request.getEmployeeId() < AppConstants.IDENTITY_COLUMN_MINVALUE) {
      throw new IllegalArgumentException("EmployeeId is required");
    }
    final Optional<EmployeeDetails> employeeDetails =
        employeeRepo.findById(request.getEmployeeId()).map(this::transform);
    if (employeeDetails.isEmpty()) {
      throw new NotFoundException("Employee not found");
    }
    responseObserver.onNext(employeeDetails.get());
    responseObserver.onCompleted();
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
