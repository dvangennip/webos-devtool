package webosdevtool.process;

import webosdevtool.Devtool;
import webosdevtool.DevSourceItem;

import java.util.Vector;

/**
 * Class manages tasks and allows addition and removal of such tasks.
 * Task themselves are handled by a separate <code>TaskHandler</code> which is notified of available tasks
 * and simply takes the first one on the list. The TaskHandler asks for a next task when done with one.
 * If no tasks are available it goes to sleep, only to be woken up by this TaskManager upon adding a new Task.
 * <br />
 * This instance also updates relevant GUI elements if necessary.
 */
public class TaskManager {
	
	// Variables
	
	protected Devtool devtool;
	
	Vector tasks;
	
	TaskHandler taskHandler;
	
	// Constructor
	
	/**
	 * @param myParent Instance of Devtool for which this class handles tasks.
	 */
	public TaskManager (Devtool myParent) {
		
		this.devtool = myParent;
		
		tasks = new Vector();
		
		taskHandler = new TaskHandler(this, devtool.fileOperator);
		taskHandler.start();
	}
	
	// Methods
	
	/**
	 * @return True if there are tasks left to be done.
	 */
	public synchronized boolean hasTasks() {
		// if any tasks left
		if ( !tasks.isEmpty() ) {
			return true;
		}
		return false;
	}
	
	/**
	 * Method returns the number of tasks related to an item, both projects and devices are
	 * supported by this method.
	 *
	 * @return Number of tasks for the specified <code>DevSourceItem</code>.
	 */
	public synchronized int getNumberOfTasksForItem(DevSourceItem itemOfInterest) {
		
		int numberOfTasks = 0;
		
		// loop over all tasks to find a match with item of interest
		for (int i = 0; i < tasks.size(); i++) {
			
			// get source item
			Task task = (Task) tasks.elementAt(i);
			
			// differentiate between projects and devices
			if ( itemOfInterest.isDevice() ) {
				// match -> testing for reference to same object
				if ( task.getDestinationDevice() == itemOfInterest) {
					numberOfTasks++;
				}
			} else {
				if ( task.getDevSourceItem() == itemOfInterest) {
					numberOfTasks++;
				}
			}
		}
		
		return numberOfTasks;
	}
	
	/**
	 * Method directly updates the task counter indicator of a project or device.
	 * It checks whether the input is <code>null</code>. If so, the call is ignored.
	 * Internally it calls <code>getNumberOfTasksForItem</code> to get the task count.
	 *
	 * @param item The desired <code>DevSourceItem</code> to update.
	 */
	public void updateItemTaskCounterIndicator(DevSourceItem item) {
		
		// make sure the item is defined
		if (item != null) {
			int c = getNumberOfTasksForItem( item );
			item.getSourceListItem().setCounterValue( c );
		}
	}
	
	/**
	 * Adds a task and interrupts the TaskHandler thread from its sleep so it can ask for the new Task.
	 * Method is synchronised to avoid concurrent modification of the tasks Vector.
	 * This method also calls <code>updateItemTaskCounterIndicator()</code> to reflect the addition.
	 *
	 * @param newTask A new Task instance to add.
	 */
	public synchronized void addTask(Task newTask) {
		// add
		tasks.add(newTask);
		
		// update bottombar
		devtool.setActivityIndicator( tasks.size() );
		
		// update task indicators
		updateItemTaskCounterIndicator( newTask.getDevSourceItem() );
		updateItemTaskCounterIndicator( newTask.getDestinationDevice() );
		
		// notify handler so it can work on the task
		synchronized (taskHandler) {
			try {
				taskHandler.notify();
			}
			catch (java.lang.IllegalMonitorStateException ims) {
				System.out.println("Manager could not wake up Handler");
			}
		}
	}
	
	/**
	 * Removes a task. Method is synchronised to avoid concurrent modification of the tasks Vector.
	 * @param taskToRemove A Task instance to remove.
	 * @to.do Handle unsuccessful tasks in a different way from successfully completed ones.
	 */
	public synchronized void removeTask(Task taskToRemove) {
		
		// check if available
		if ( tasks.contains(taskToRemove) ) {

			// if not active remove
			if ( !taskToRemove.isActive() ) {
				// remove
				tasks.remove(taskToRemove);
				
				// update bottombar
				devtool.setActivityIndicator( tasks.size() );
				
				// update task indicators
				updateItemTaskCounterIndicator( taskToRemove.getDevSourceItem() );
				updateItemTaskCounterIndicator( taskToRemove.getDestinationDevice() );
			}
		}
	}
	
	/**
	 * Returns a Task to be processed. The TaskHandler asks for a next task when done with one.
	 * If no tasks are available it goes to sleep, only to be woken up by this TaskManager upon adding a new Task.
	 * Method is synchronised to avoid concurrent modification of the tasks Vector.
	 * @return Task instance which has not yet been completed and is not active yet.
	 */
	public synchronized Task getNextTask() {
		// if any tasks left
		if ( !tasks.isEmpty() ) {
			
			// loop over tasks to find a suitable one
			// that is an inactive, uncompleted task
			for (int i = 0; i < tasks.size(); i++) {
				
				Task nextTask = (Task) tasks.firstElement();
				
				// when ok return
				if ( !nextTask.isCompleted() && !nextTask.isActive() ) {
					return nextTask;
				}
			}
		}
		
		// else it returns null
		return null;
	}
}