package com.myhousestair.myhousestair.domain

import com.myhousestair.myhousestair.domain.common.PrimaryKeyEntity
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "MEMBER_TABLE")
class Member(
    email: String,
    password: String
) : PrimaryKeyEntity(), UserDetails {
    @Column(nullable = false, unique = true)
    var email: String = email
        protected set

    @Column(nullable = false)
    var pass: String = password
        protected set

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member", cascade = [CascadeType.ALL])
    private val _contractInventory: MutableSet<ContractInventory> = mutableSetOf()
    val contractInventory: List<ContractInventory>
        get() = _contractInventory.toList()


    @ElementCollection
    private val roles: List<String> = mutableListOf()

    override fun getAuthorities() = roles.stream()
        .map(::SimpleGrantedAuthority)
        .toList() as MutableCollection<out GrantedAuthority>

    override fun getPassword() = this.pass

    override fun getUsername() = this.email

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    override fun isCredentialsNonExpired() = true

    override fun isEnabled() = true

    fun addContractInventory(contractInventory: ContractInventory) {
        _contractInventory.add(contractInventory)
    }
}