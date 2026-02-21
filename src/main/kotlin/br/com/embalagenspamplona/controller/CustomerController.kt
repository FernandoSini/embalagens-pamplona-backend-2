package br.com.embalagenspamplona.controller

import br.com.embalagenspamplona.data.dto.*
import br.com.embalagenspamplona.exceptions.InternalServerException
import br.com.embalagenspamplona.services.AddressService
import br.com.embalagenspamplona.services.CustomerService
import br.com.embalagenspamplona.services.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import okhttp3.Response
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Clientes", description = "Gerenciamento de clientes")
class CustomerController(
    private val customerService: UserService,
    private val addressService: AddressService,
) {

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID")
    fun findById(@PathVariable id: Long): ResponseEntity<ApiResponse<UserDTO>> {
        val customer = customerService.findById(id)
        if (customer == null) return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error("Usuario não encontrado para esse id!"))
        return ResponseEntity.ok(ApiResponse.success(customer))
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Buscar cliente por e-mail")
    fun findByEmail(@PathVariable email: String): ResponseEntity<ApiResponse<UserDTO>> {
        val customer = customerService.findByEmail(email)
        if (customer == null) return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error("Usuário não encontrado para esse email"))
        return ResponseEntity.ok(ApiResponse.success(customer))
    }


    @PutMapping("/update")
    @Operation(summary = "Atualizar cliente")
    fun update(@RequestBody request: UserDTO): ResponseEntity<ApiResponse<String>> {
        try {
            val isUpdated = customerService.update(request)
            if (isUpdated) {

                return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(data = "", message = "success"))
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.success(data = "Não atualizou!"))
            }
        } catch (e: Exception) {
            throw e
        }
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Excluir cliente (soft delete)")
    fun delete(@RequestBody userId: Long): ResponseEntity<ApiResponse<Unit>> {
        val isDeleted = customerService.deleteUser(userId)
        if (isDeleted) {
            return ResponseEntity.ok(ApiResponse.success(Unit, "Cliente excluído com sucesso"))
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Não foi possível excluir este usuário"))
        }
    }

    @GetMapping("/{customerId}/addresses")
    @Operation(summary = "Listar endereços do cliente")
    fun getAddresses(@PathVariable customerId: Long): ResponseEntity<ApiResponse<List<AddressDTO>>> {
        val addresses = addressService.getUserAddresses(customerId)
        return ResponseEntity.ok(ApiResponse.success(addresses))
    }

    @PostMapping("/{customerId}/addresses")
    @Operation(summary = "Adicionar endereço ao cliente")
    fun addAddress(
        @PathVariable customerId: Long,
        @RequestBody request: CreateAddressRequest
    ): ResponseEntity<ApiResponse<AddressDTO>> {
        val address = addressService.addAddress(request, customerId)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(address, "Endereço adicionado com sucesso"))
    }

    @PutMapping("/updateAddress")
    @Operation(summary = "Atualizar endereço do cliente")
    fun updateAddress(
        @RequestBody request: updateAddressRequest
    ): ResponseEntity<ApiResponse<AddressDTO>> {
        val addressDTO = AddressDTO(
            id = request.addressId,
            city = request.city,
            number = request.complementNumber,
            complementNumber = request.unityBlock,
            street = request.street,
            neighborhood = request.neighborhood,
            state = request.state,
            zipCode = request.zipCode,
            reference = request.reference,
        )
        val address = addressService.updateUserAddress(request.userId, addressDTO)
        return ResponseEntity.ok(ApiResponse.success(address!!, "Endereço atualizado com sucesso"))
    }

    @DeleteMapping("/{customerId}/addresses/{addressId}")
    @Operation(summary = "Excluir endereço do cliente")
    fun deleteAddress(
        @PathVariable customerId: Long,
        @PathVariable addressId: Long
    ): ResponseEntity<ApiResponse<Unit>> {
        try {


            val isDeleted = addressService.deleteAddress(customerId, addressId)
            if (isDeleted) {
                return ResponseEntity.ok(ApiResponse.success(Unit, "Endereço excluído com sucesso"))
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Não foi possível deletar o endereço"))
            }
        } catch (e: Exception) {
            throw InternalServerException(e)
        }
    }

    @PatchMapping("/addresses/default")
    @Operation(summary = "Definir endereço como padrão")
    fun setDefaultAddress(
        @RequestBody data: HashMap<String, Any>
    ): ResponseEntity<ApiResponse<String>> {
        val isDefault = addressService.setDefaultAddress(
            data["customerId"].toString().toLong(),
            data["addressId"].toString().toLong()
        )
        if (isDefault) {
            return ResponseEntity.ok(ApiResponse.success("", "Endereço definido como padrão"))
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Não foi possivel alterar para padrão"))
        }
    }

    @GetMapping("/check-email")
    @Operation(summary = "Verificar se e-mail já está cadastrado")
    fun checkEmail(@RequestBody email: String): ResponseEntity<ApiResponse<String>> {
        val exists = customerService.findByEmail(email)
        if (exists != null) {
            return ResponseEntity.ok(ApiResponse.success("Usuário já existe"))
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("E-mail não cadastrado!"))
        }
    }
}
