package com.vadim212.securityfilesharingapp.presentation.view.fragment

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.vadim212.securityfilesharingapp.presentation.di.HasComponent
import kotlin.reflect.KClass
import kotlin.reflect.cast

abstract class BaseFragment: Fragment() {
    protected fun showToastMessage(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    protected fun <C : Any> getComponent(componentType: KClass<C>): C { // TODO: check and remove Any and KClass if needed
        //return componentType.cast((activity as HasComponent<C>).getComponent())
        return componentType.cast((this as HasComponent<C>).getComponent())
    }


}