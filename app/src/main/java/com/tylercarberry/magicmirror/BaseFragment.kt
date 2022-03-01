package com.tylercarberry.magicmirror

import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<BINDING: ViewBinding>: Fragment() {
    protected lateinit var binding: BINDING
}
