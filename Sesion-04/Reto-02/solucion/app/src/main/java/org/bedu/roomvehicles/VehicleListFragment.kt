package org.bedu.roomvehicles

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.bedu.roomvehicles.room.BeduDb
import org.bedu.roomvehicles.room.ReducedVehicle
import org.bedu.roomvehicles.room.Vehicle
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


/**
 * A fragment representing a list of Items.
 */
class VehicleListFragment : Fragment(), ItemListener {


    private lateinit var addButton: FloatingActionButton
    private lateinit var recyclerVehicle: RecyclerView
    private lateinit var adapter: VehicleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    fun getListener(): ItemListener{
        return this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_vehicle_list, container, false)

        addButton = view.findViewById(R.id.button_add)
        recyclerVehicle = view.findViewById(R.id.list)

        addButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_vehicleListFragment_to_addEditFragment
            )
        }


        val executor: ExecutorService = Executors.newSingleThreadExecutor()

        executor.execute(Runnable {

            val vehicleArray = BeduDb
                .getInstance(context = requireContext())
                ?.vehicleDao()
                ?.getReducedVehicles() as MutableList<ReducedVehicle>

            Handler(Looper.getMainLooper()).post(Runnable {
                adapter = VehicleAdapter(vehicleArray, getListener())
                recyclerVehicle.adapter = adapter
            })
        })

        return view
    }

    override fun onEdit(vehicle: ReducedVehicle) {
        findNavController().navigate(
                R.id.action_vehicleListFragment_to_addEditFragment,
                bundleOf("vehicle_id" to vehicle.id )
        )
    }

    override fun onDelete(vehicle: ReducedVehicle) {
        Executors
            .newSingleThreadExecutor()
            .execute(Runnable {
                BeduDb
                    .getInstance(context = requireContext())
                    ?.vehicleDao()
                    ?.removeVehicleById(vehicle.id)

                Handler(Looper.getMainLooper()).post(Runnable {
                    adapter.removeItem(vehicle)
                    Toast.makeText(context,"Elemento eliminado!",Toast.LENGTH_SHORT).show()
                })
            })
    }

}