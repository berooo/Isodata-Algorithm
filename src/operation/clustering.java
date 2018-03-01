package operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import entity.category;
import entity.point;

public class clustering {

	private List<point> ls;
	private List<category> c;

	// 希望的聚类中心的数目
	int K;
	// 每个聚类中应具有的最少样本数
	int On;
	// 一个聚类域中样本距离分布的标准差阈值
	int Os;
	// 两聚类中心之间的最小距离
	int Oc;
	// 在一次迭代中允许合并的聚类中心的最大对数
	int L;
	// 允许迭代的次数
	int I;
	// 迭代次数
	int iterations;
	// 聚类中心数目
	int Nc;
	// 分裂系数
	float k;
	// 最大分量下标集合
	int[] index;
	// 是否改变输入
	boolean changeInput;

	public clustering(List<point> ls, boolean changeInput) {
		this.ls = ls;
		this.changeInput = changeInput;
		c = new ArrayList<category>();
		iterations = 0;
	}

	// 第一步：输入参数
	public void setArguments(int K, int On, int Os, int L, int I, int Oc, int Nc, float k) {
		this.K = K;
		this.On = On;
		this.Os = Os;
		this.L = L;
		this.I = I;
		this.Os = Os;
		this.Oc = Oc;
		this.Nc = Nc;
		this.k = k;
	}

	// 第二步：初始化，确定初始聚类中心
	public void initial() {

		Random r = new Random();
		for (int i = 0; i < Nc; i++) {

			int num = Math.abs(r.nextInt() % ls.size());
			point a = ls.get(num);
			category ca = new category();
			ca.setCenter(a);
			c.add(ca);

		}

	}

	public float Euclidean(point a, point b) {

		float[] aValue = a.getValue();
		float[] bValue = b.getValue();

		float sum = 0;
		for (int i = 0; i < aValue.length; i++) {
			sum += Math.pow((aValue[i] - bValue[i]), 2);
		}

		return (float) Math.sqrt(sum);

	}

	public int choose(List<category> ls, point a) {

		float min = Float.MAX_VALUE;
		int index = 0;

		for (int i = 0; i < ls.size(); i++) {
			float distance = Euclidean(ls.get(i).getCenter(), a);
			if (distance <= min) {
				min = distance;
				index = i;
			}
		}

		return index;
	}

	// 第三步：近邻聚类，并修正聚类中心的值
	public void cluster() {

		for (int i = 0; i < c.size(); i++) {
			for (int j = 0; j < c.get(i).getList().size(); j++) {
				c.get(i).getList().remove(j);
			}
		}

		for (int i = 0; i < ls.size(); i++) {
			point a = ls.get(i);
			int ind = choose(c, a);
			category ca = c.get(ind);
			if (!ca.checkExist(a)) {
				ca.add(a);
			}
		}
		point[] points = null;
		boolean flag = false;
		for (int i = 0; i < c.size(); i++) {
			if (c.get(i).getList().size() < On) {
				points = new point[c.get(i).getList().size()];
				for (int j = 0; j < c.get(i).getList().size(); j++) {
					points[j] = c.get(i).getList().get(j);
				}
				c.remove(i);
				flag = true;
				break;
			}
			c.get(i).updateCenter();
		}
		if (flag) {
			allocate(points);
			for(int i=0;i<c.size();i++) {
				c.get(i).updateCenter();
			}
		}
	}

	public boolean allocate(point[] p) {
 
		for (int i = 0; i < p.length; i++) {
			point a = p[i];
			int ind = choose(c, a);
			category ca = c.get(ind);
			if (!ca.checkExist(a)) {
				ca.add(a);
			}
		}
		return false;
	}

	public float calTotalMeanDistance() {

		float sum = 0;
		for (int i = 0; i < c.size(); i++) {
			sum += c.get(i).calMeanDistance();
		}
		return sum / c.size();

	}

	// 判决是进行分裂还是合并
	public int judge() {

		if ((iterations >= I)) {
			return 1;
		}
		if (c.size() <= K / 2) {
			return 0;
		}

		if ((iterations % 2 == 0) || (c.size() - 2 * K) > 0) {
			return 1;
		} else {
			return 0;
		}

	}

	// 分裂
	public boolean division() {

		boolean flag = false;
		float[] vectorSet = maximumComponentSet();
		for (int i = 0; i < vectorSet.length; i++) {
			if (vectorSet[i] > Os) {

				float intraDis = c.get(i).calMeanDistance();
				float totalDis = calTotalMeanDistance();
				int Nj = c.get(i).getList().size();
				if ((intraDis > totalDis) || (Nj < K / 2)) {

					point[] points = calNewClusterCenter(i, vectorSet[i]);
					c.remove(i);
					category z1 = new category();
					z1.setCenter(points[0]);
					category z2 = new category();
					z2.setCenter(points[1]);
					c.add(z1);
					c.add(z2);

					flag = true;
					break;

				}
			}
		}
		return flag;
	}

	// 合并
	@SuppressWarnings("unchecked")
	public boolean merge() {
		boolean flag = false;
		float[][] distance = new float[c.size()][c.size()];
		List<float[]> set = new ArrayList<float[]>();
		for (int i = 0; i < c.size(); i++) {
			for (int j = i + 1; j < c.size(); j++) {
				distance[i][j] = Euclidean(c.get(i).getCenter(), c.get(j).getCenter());
				if (distance[i][j] < Oc) {
					float[] save = new float[3];
					save[0] = distance[i][j];
					save[1] = i;
					save[2] = j;
					set.add(save);
					flag = true;
				}
			}
		}
		if (flag) {
			Collections.sort(set, new sort());
			float[] ex1 = set.get(0);
			int index1 = (int) ex1[1];
			int index2 = (int) ex1[2];

			category ca = c.get(index1);
			point center1 = ca.getCenter();
			float[] f1 = center1.getValue();
			category cb = c.get(index2);
			point center2 = cb.getCenter();
			float[] f2 = center2.getValue();
			float[] f3 = new float[f1.length];
			for (int i = 0; i < f1.length; i++) {
				f3[i] = (c.get(index1).getList().size() * f1[i] + c.get(index2).getList().size() * f2[i])
						/ (c.get(index1).getList().size() + c.get(index2).getList().size());
			}

			point center3 = new point();
			center3.setValue(f3);

			category cate = new category();
			cate.setCenter(center3);

			c.remove(ca);
			c.remove(cb);
			c.add(cate);

			Nc -= 1;
		}

		return flag;
	}

	public point[] calNewClusterCenter(int indexs, float a) {

		point[] points = new point[2];
		category cate = c.get(indexs);

		float[] f = cate.calStandardVectorDifference();

		int j = index[indexs];
		f[j] += k * a;

		point p1 = new point();
		p1.setValue(f);
		points[0] = p1;

		f[j] -= 2 * k * a;

		point p2 = new point();
		p2.setValue(f);
		points[1] = p2;

		// c.remove(cate);
		return points;
	}

	// 求最大分量集
	public float[] maximumComponentSet() {
		float set[] = new float[c.size()];
		index = new int[c.size()];
		for (int i = 0; i < c.size(); i++) {
			category cate = c.get(i);
			float[] vectorSet = cate.calStandardVectorDifference();
			float max = Float.MIN_VALUE;
			for (int j = 0; j < vectorSet.length; j++) {
				if (vectorSet[j] >= max) {
					max = vectorSet[j];
					index[i] = j;
				}
			}
			set[i] = max;
		}
		return set;
	}

	public int run(int K, int On, int Os, int L, int I, int Oc, int Nc, float k) {
		setArguments(K, On, Os, L, I, Oc, Nc, k);
		initial();
		boolean isDivisionSuccess = false;
		boolean isMergeSuccess = true;
		cluster();
		iterations += 1;
		do {
			isDivisionSuccess = false;
			isMergeSuccess = true;
			
			int key = judge();
			if (key == 1) {
				isMergeSuccess = merge();
				cluster();
				iterations += 1;
				if ((isMergeSuccess == false) || (iterations < I)) {
					if (changeInput) {
						break;
					}
				}
			} else {
				isDivisionSuccess = division();
				cluster();
				iterations += 1;
				
			}
		} while ((isDivisionSuccess == true || isMergeSuccess == false) && (iterations < I));

		if (iterations < I) {
			return 1;
		} else {
			return 0;
		}
	}

	public void printResult() {
		for (int i = 0; i < c.size(); i++) {
			String name = "Z" + (i + 1);
			c.get(i).setName(name);
			System.out.println(c.get(i).getName());
			List<point> l = c.get(i).getList();
			for (int j = 0; j < l.size(); j++) {
				point p = l.get(j);
				System.out.println(Arrays.toString(p.getValue()));
			}
		}
	}
}
