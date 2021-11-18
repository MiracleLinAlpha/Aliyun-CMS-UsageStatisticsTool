package entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY, getterVisibility=JsonAutoDetect.Visibility.NONE)

public class MountInstancesEty {
	private List<Object> MountInstance;

	public MountInstancesEty() {
		super();
	}

	public List<Object> getMountInstance() {
		return MountInstance;
	}

	public void setMountInstance(List<Object> mountInstance) {
		MountInstance = mountInstance;
	}
}
