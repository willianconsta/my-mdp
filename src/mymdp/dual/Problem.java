package mymdp.dual;

public interface Problem<M, C>
{
	String getName();

	M getModel();

	C getComplement();
}
