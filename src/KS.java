import java.util.*;
public class KS {
	int generations,numberOfItems,sizeOfKnapSack;																			
	int w[];
	int b[];
	char finalSolution[];
	int numberOfPopulation=8;
	char population[][]; 
	int totalBenefit[]=new int[numberOfPopulation];
	int totalWeight[]=new int[numberOfPopulation];
	Vector <char[]> childs=new Vector<>();
	public void settestCases(int generations)
	{
		this.generations=generations;
	}
	public void setnumberOfItems(int numberOfItems)
	{
		this.numberOfItems=numberOfItems;
	}
	public void setsizeOfKnapSack(int sizeOfKnapSack)
	{
		this.sizeOfKnapSack=sizeOfKnapSack;
	}
	public KS()
	{}
	private void getInputs()
	{
		Scanner input = new Scanner(System.in);
		System.out.print("Please Enter Number of Generations: ");
		settestCases(input.nextInt());
		setnumberOfItems(input.nextInt());
		setsizeOfKnapSack(input.nextInt());
		w=new int[numberOfItems];
		b=new int[numberOfItems];
		finalSolution=new char[numberOfItems];
		population=new char[numberOfPopulation][numberOfItems];
		for(int i=0;i<numberOfItems;i++)
		{
			w[i]=input.nextInt();
			b[i]=input.nextInt();
		}
	}
	public double randomGenerator()
	{
		return Math.random();
	}
	private char[] getChromosome()
	{
		char Chromosome[]=new char[numberOfItems];
		for(int i=0;i<numberOfItems;i++)
		{
			if(randomGenerator()<0.5)
			{
				Chromosome[i]='1';
			}
			else
			{
				Chromosome[i]='0';
			}
		}
		if(feasible(Chromosome))
		{
			return Chromosome;
		}
		return getChromosome();
	}
	private int fitness(char chromosome[])
	{
		int totalWeight=0;
		for(int i=0;i<numberOfItems;i++)
		{
			if(chromosome[i]=='1')
			{
				totalWeight+=w[i];
			}
		}
		return totalWeight;
	}
	private int getBenefit(char chromosome[]) {
		int Benefit=0;
		for(int i=0;i<numberOfItems;i++)
		{
			if(chromosome[i]=='1')
			{
				Benefit+=b[i];
			}
		}
		return Benefit;
	}
	private boolean feasible(char chromosome[])
	{
		if(fitness(chromosome)>sizeOfKnapSack||redundant(chromosome))
		{
			return false;
		}
		return true;
	}
	private boolean redundant(char chromosome[])
	{
		boolean statusOfRedundancy=false;
		for(int i=0;i<numberOfPopulation;i++)
		{
			for(int j=0;j<numberOfItems;j++)
			{
				if(chromosome[j]!=population[i][j])
				{
					break;
				}
				if(chromosome[j]==population[i][j]&&j==numberOfItems-1)
				{
					statusOfRedundancy=true;
					break;
				}
			}
		}
		return statusOfRedundancy;
	}
	private char[] initializePopulation(int number)
	{
		if(number==1)
		{
			for(int i=0;i<numberOfPopulation;i++)
			{
				population[i]=getChromosome();
			}
		}
		else
		{
			return getChromosome();
		}
		return null;
	}
	private void perform() {
		getInputs();
		initializePopulation(1);
		for(int i=0;i<generations;i++)
		{
			for(int j=0;j<numberOfPopulation;j++)
			{
				totalBenefit[j]=getBenefit(population[j]);
			}
			System.out.println("Iteration #"+i);
			sort();
//			showPopulation();
			performSelection(); ///Elitism is keeping the best solution away from crossover or mutation
			sortChilds();
			performReplacement();
			childs.clear();			
		}
		//makeSure();
		getFinalResult();
	}
	private void performSelection() {
		for(int i=0;i<numberOfPopulation;i++)
		{
			int m=performRoulette();
			int n=performRoulette();
			while(n==m)
			{
				n=performRoulette();
			}
//			System.out.println("N = "+n+" M = "+m);
			performCrossOver(n,m);
		}
	}
	private int performRoulette()
	{
		double totalBenefits = 0;
		for(int i=0;i<numberOfPopulation;i++)
		{
			totalBenefits+=getBenefit(population[i]);
		}
		double random=randomGenerator();
//		System.out.println("random = "+random);
		double sum=0.0;
		for(int i=0;i<numberOfPopulation;i++)
		{
			if(random>sum)
			{
				sum+=getBenefit(population[i])/totalBenefits;
			}
			else
			{
				return i;
			}
		}
		return numberOfPopulation-1;
		
	}
	private void performCrossOver(int i, int j) {
		char firstChild[]=new char[numberOfItems];
		char secondChild[]=new char[numberOfItems];
		for(int k=0;k<(numberOfItems/2);k++)
		{
			firstChild[k]=population[i][k];
			secondChild[k]=population[j][k];
		}
		for(int k=(numberOfItems/2);k<numberOfItems;k++)
		{
			firstChild[k]=population[j][k];
			secondChild[k]=population[i][k];
		}
		if(getBenefit(firstChild)>getBenefit(secondChild))
		{
			firstChild=performMutation(firstChild);
		}
		else
		{
			secondChild=performMutation(secondChild);
		}
		childs.add(firstChild);
		childs.add(secondChild);
	}
	private char[] performMutation(char[] child) {
		for(int i=0;i<numberOfItems;i++)
		{
			if(child[i]=='0')
			{
				if(randomGenerator()<0.01)
				{
					child[i]='1';
//					System.out.println("Mutation Happened");
				}
			}
			else
			{
				if(randomGenerator()<0.01)
				{
					child[i]='0';
//					System.out.println("Mutation Happened");
				}
			}
		}
		return child;
	}
	private void performReplacement()
	{
		for(int i=1;i<numberOfPopulation;i++)
		{
			if(feasible(childs.elementAt(i)))
			{
				population[i]=childs.elementAt(i);
			}
			else
			{
				population[i]=initializePopulation(3);
			}
		}
		sort();
	}
	private void Transfer(int i,int j)
	{
		char temp[]=new char[numberOfItems];
		for(int k=0;k<numberOfItems;k++)
		{
			temp[k]=population[i][k];
			population[i][k]=population[j][k];
			population[j][k]=temp[k];
		}
	}
	private void sort() {
		for(int i=0;i<numberOfPopulation;i++)
		{
			for(int j=i+1;j<numberOfPopulation;j++)
			{
				if(totalBenefit[i]<totalBenefit[j])
				{
					Transfer(i,j);
					int temp=0;
					temp=totalBenefit[i];
					totalBenefit[i]=totalBenefit[j];
					totalBenefit[j]=temp;
				}
			}
		}
	}
	private void sortChilds() {
		for(int i=0;i<childs.size();i++)
		{
			for(int j=i+1;j<childs.size();j++)
			{
				if(getBenefit(childs.elementAt(i))<getBenefit(childs.elementAt(j)))
				{
					char[] temp=childs.elementAt(i);
					childs.set(i, childs.elementAt(j));
					childs.set(j,temp);
				}
			}
		}
	}
	private void getFinalResult() {
		System.out.print("Total Benefit = "+getBenefit(population[0])+"\n");
		for(int i=0;i<numberOfItems;i++)
		{
			if(population[0][i]=='1')
			{
				System.out.print("Item of "+(i+1)+"Which it's weight & Benefit "+ w[i]+" "+b[i]+" Was Taken");
			}
		}
	}
	public void makeSure()
	{
		System.out.print("# Of generations= "+generations+"\n"+
						"# Of Items= "+numberOfItems+"\n"+
						"Size Of KnapSack= "+sizeOfKnapSack+"\n");
		for(int i=0;i<numberOfItems;i++)
		{
			System.out.print("Weight & benefit of "+ (i+1) +" item is "+w[i]+" "+b[i]+"\n");
		}
		System.out.print("final Solution is ");
		for(int i=0;i<numberOfItems;i++)
		{				
			System.out.print(finalSolution[i]+" ");
		}
		System.out.print("\n");
		showPopulation();
	}
	public void showPopulation()
	{
		for(int i=0;i<numberOfPopulation;i++)
		{
			System.out.print("# "+ (i+1) + " is ");
			for(int j=0;j<numberOfItems;j++)
			{
				System.out.print(population[i][j]+" ");
			}
			System.out.print("With total benefit = "+getBenefit(population[i]));
			System.out.print("\n");
		}
		System.out.print("\n");
	}
	public static void main(String[] args) {
			KS object=new KS();
			object.perform();	
	}
}
