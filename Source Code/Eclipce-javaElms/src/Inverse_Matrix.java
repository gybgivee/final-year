import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.Matrices;
import no.uib.cipr.matrix.NotConvergedException;
import no.uib.cipr.matrix.SVD;

public class Inverse_Matrix {
	private DenseMatrix A1;	
	private int m;
	private int n;
	
	public Inverse_Matrix(DenseMatrix AD){
		m = AD.numRows();
		n = AD.numColumns();	
		A1 = AD.copy();
	}
	
	//Just the inverse maxtrix
	public DenseMatrix getInverse_Matrix(){
	
		DenseMatrix I = Matrices.identity(n);
		DenseMatrix Ainv = I.copy();
		A1.solve(I, Ainv);
	
		return Ainv;
	}
	
	/*	Moore-Penrose generalized inverse maxtrix
	 *  Theory:Full rank factorization
	 *	[U S Vt] = SVD(A) <==> U*S*Vt = A
	 *	C=U*sqrt(S)		D=sqrt(S)*Vt <==> A=C*D,Full rank factorization
	 *	MP(A) = D'*inv(D*D')*inv(C'*C)*C'
	 */
	public DenseMatrix getMPInverse_Matrix() throws NotConvergedException{
		SVD svd= new SVD(m,n);		//U*S*Vt=A;
		svd.factor(A1);
		DenseMatrix U = svd.getU();		//m*m
		DenseMatrix Vt = svd.getVt();	//n*n
		double []s = svd.getS();
		int sn = s.length;
		for (int i = 0; i < sn; i++) {
			s[i] = Math.sqrt(s[i]);
		}
		
		DenseMatrix S1 = (DenseMatrix) Matrices.random(m, sn);
		S1.zero();
		DenseMatrix S2 = (DenseMatrix) Matrices.random(sn, n);
		S2.zero();
		for (int i = 0; i < s.length; i++) {
			S1.set(i, i, s[i]);
			S2.set(i, i, s[i]);
		}
		
		DenseMatrix C = new DenseMatrix(m,sn);
		U.mult(S1, C);
		DenseMatrix D = new DenseMatrix(sn,n);
		S2.mult(Vt,D);
		
		DenseMatrix DD = new DenseMatrix(sn,sn);
		DenseMatrix DT = new DenseMatrix(n,sn);
		D.transpose(DT);
		D.mult(DT, DD);
		Inverse_Matrix inv = new Inverse_Matrix(DD);
		DenseMatrix invDD = inv.getInverse_Matrix();
		
		DenseMatrix DDD = new DenseMatrix(n,sn);
		DT.mult(invDD, DDD);
		
		DenseMatrix CC = new DenseMatrix(sn,sn);
		DenseMatrix CT = new DenseMatrix(sn,m);
		C.transpose(CT);
		
		CT.mult(C, CC);
		Inverse_Matrix inv2 = new Inverse_Matrix(CC);
		DenseMatrix invCC = inv2.getInverse_Matrix();
		
		DenseMatrix CCC = new DenseMatrix(sn,m);
		invCC.mult(CT, CCC);
		
		DenseMatrix Ainv = new DenseMatrix(n,m);
		DDD.mult(CCC, Ainv);
		return Ainv;
	}
	/*	Moore-Penrose generalized inverse maxtrix
	 * 	Theory:Ridge regression
	 *	MP(A) = inv((H'*H+lumda*I))*H'
	 */
	public DenseMatrix getMPInverse_Matrix(double lumda) throws NotConvergedException{
		DenseMatrix At = new DenseMatrix(n, m);
		A1.transpose(At);
		DenseMatrix AtA = new DenseMatrix(n ,n);
		At.mult(A1,AtA);

		DenseMatrix I = Matrices.identity(n);
		AtA.add(lumda, I);
		DenseMatrix AtAinv = I.copy();
		AtA.solve(I, AtAinv);
		
		DenseMatrix Ainv = new DenseMatrix(n,m);
		AtAinv.mult(At, Ainv);
	
		return Ainv;
	}
	public DenseMatrix checkCD() throws NotConvergedException{
		SVD svd= new SVD(m,n);		//U*S*Vt=A;
		svd.factor(A1);
		DenseMatrix U = svd.getU();		//m*m
		DenseMatrix Vt = svd.getVt();	//n*n
		double []s = svd.getS();
		int sn = s.length;

		for (int i = 0; i < s.length; i++) {
			s[i] = Math.sqrt(s[i]);
		}
		
		DenseMatrix S1 = (DenseMatrix) Matrices.random(m, sn);
		S1.zero();
		DenseMatrix S2 = (DenseMatrix) Matrices.random(sn, n);
		S2.zero();
		for (int i = 0; i < s.length; i++) {
			S1.set(i, i, s[i]);
			S2.set(i, i, s[i]);
		}
		
		DenseMatrix C = new DenseMatrix(m,sn);
		U.mult(S1, C);
		DenseMatrix D = new DenseMatrix(sn,n);
		S2.mult(Vt,D);
		
		DenseMatrix CD = new DenseMatrix(m,n);
		C.mult(D, CD);
		
		return CD;
	}
	

}
