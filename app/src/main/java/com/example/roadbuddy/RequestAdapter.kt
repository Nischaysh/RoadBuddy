import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.roadbuddy.R

data class Request(val service: String, val status: String, val helpername :String)

class RequestAdapter(private var requestList: List<Request>) :
    RecyclerView.Adapter<RequestAdapter.RequestViewHolder>() {

    inner class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val serviceTextView: TextView = itemView.findViewById(R.id.serviceTextView)
        val statusTextView: TextView = itemView.findViewById(R.id.statusTextView)
        val helperTextView: TextView = itemView.findViewById(R.id.helpernameText)
        val statusIcon: ImageView = itemView.findViewById(R.id.statusIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_request, parent, false)
        return RequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val request = requestList[position]
        holder.serviceTextView.text = request.service
        holder.statusTextView.text = request.status

        if (request.helpername == "Not assign"){
            holder.helperTextView.text = request.helpername
        }
        else{
            holder.helperTextView.text = "By "+request.helpername
        }

        if (request.status == "pending") {
            holder.statusIcon.setImageResource(R.drawable.pending)  // Use pending icon
        } else if (request.status == "accepted") {
            holder.statusIcon.setImageResource(R.drawable.accept)  // Use accepted icon
        }


    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    fun updateRequests(newRequests: List<Request>) {
        requestList = newRequests
        notifyDataSetChanged()  // Refresh the RecyclerView with new data
    }
}
