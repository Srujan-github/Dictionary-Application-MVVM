package labs.creative.dictornarymvvm.core

import androidx.fragment.app.Fragment

/**
 * Base class for all Fragments in the app.
 * Extend this instead of [Fragment] directly to allow shared behaviour
 * (analytics, logging, common error handling) to be added here in future
 * without touching every fragment.
 */
abstract class BaseFragment : Fragment()
