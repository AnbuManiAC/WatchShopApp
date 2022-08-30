package com.sample.chrono12.data.entities.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.sample.chrono12.data.entities.Address
import com.sample.chrono12.data.entities.AddressAndGroupCrossRef
import com.sample.chrono12.data.entities.AddressGroup

data class AddressGroupWithAddress(
    @Embedded val addressGroup: AddressGroup,
    @Relation(
        parentColumn = "addressGroupId",
        entity = Address::class,
        entityColumn = "addressId",
        associateBy = Junction(
            value = AddressAndGroupCrossRef::class,
            parentColumn = "addressGroupId",
            entityColumn = "addressId"
        )
    )
    val addressList: List<Address>
)
