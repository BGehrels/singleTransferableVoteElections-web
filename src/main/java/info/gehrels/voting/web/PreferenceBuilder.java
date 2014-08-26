package info.gehrels.voting.web;

public final class PreferenceBuilder {
	private Long value;

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if ((o == null) || (getClass() != o.getClass())) {
			return false;
		}

		PreferenceBuilder that = (PreferenceBuilder) o;

		return !((value != null) ? !value.equals(that.value) : (that.value != null));

	}

	@Override
	public int hashCode() {
		return (value != null) ? value.hashCode() : 0;
	}
}
