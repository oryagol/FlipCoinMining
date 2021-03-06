package boundry;


import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import Utils.Combination;
import Utils.Status;
import control.CombinationCreator;
import control.CreateLottery;
import control.DataHandler;
import control.DeleteObject;
import control.ExecuteLottery;
import control.ImportRiddles;
import control.LoginLogic;
import control.SolveRiddle;
import control.TransactionSelection;
import control.UpdateIrreleventStatus;
import control.UpdateLevel;
import entity.Benefit;
import entity.Block;
import entity.DifficultyLevel;
import entity.Lottery;
import entity.Riddle;
import entity.SolvingMiner;
import entity.Transaction;
import exceptions.AnswerException;
import exceptions.RegisterException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FrmMining  implements Initializable{
	@FXML
	private Button deleteBenefit;

	@FXML
	private Button addBenefit;

	@FXML
	private ComboBox<String> BenefitToDelete;

	@FXML
	private TextField benefitToAdd;

	@FXML
	private Button deleteLevel;

	@FXML
	private ComboBox<String> levelToDelete;

	@FXML
	private Button deleteLottery;

	@FXML
	private ComboBox<String> loteryToDelete;

	@FXML
	private Button deleteRiddle;

	@FXML
	private ComboBox<String> riddleToDelete;


	@FXML
	private ComboBox<String> riddleDiff;

	@FXML
	private ComboBox<String> riddlesRiddle;

	@FXML
	private Button riddleStart;

	@FXML
	private Button riddleSend;

	@FXML
	private TextArea riddleSol;

	@FXML
	private TextArea riddleRiddle;

	@FXML
	private ComboBox<String> blocksMyBlocks;

	@FXML
	private ComboBox<String> blocksTrans;

	@FXML
	private ComboBox<String> blocksAddTrans;
	@FXML
	private ComboBox<String> riddlesUnassigned;

	@FXML
	private ComboBox<String> unassignedDifficulty;

	@FXML
	private TextField addDifficulty;

	@FXML
	private TextField addLevel;

	@FXML
	private TextField addBlockSize;

	@FXML
	private ComboBox<String> updateDifficulty;

	@FXML
	private TextField updateBlockSize;

	@FXML
	private TextField timeLeft;

	@FXML
	private TextField blocksSum;
	private DataHandler handler;
	private CombinationCreator comb;
	private ArrayList<Block> blocks;
	private ArrayList<Combination> combinations;
	private TransactionSelection select;
	private SolveRiddle r;
	private ArrayList<DifficultyLevel> dLevels;
	private ArrayList<Riddle> riddles;
	private ArrayList<Lottery> lotteries;
	private ArrayList<Benefit> benefits;
	private HashMap<String, Riddle> riddlesByDis;
	private HashMap<String, DifficultyLevel> difiByDis;
	private UpdateLevel u;
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		//Manage Blocks
		comb=new CombinationCreator(); 
		r = new SolveRiddle();

		blocks=new ArrayList<Block>(comb.getReleventBlocks(LoginLogic.getMiner().getAddress()));
		ObservableList<String> uList=FXCollections.observableArrayList();
		for(Block b:blocks)
		{
			uList.add(b.getAddress());
		}

		blocksMyBlocks.setItems(uList);

		ArrayList<Transaction> transact=comb.getTransactions();
		ObservableList<String> tList=FXCollections.observableArrayList();
		for(Transaction t:transact)
		{
			tList.add(t.getId());
		}
		blocksAddTrans.setItems(tList);
		HashMap<String, DifficultyLevel> dl = new HashMap<>();
		dLevels = new ArrayList<>();
		dLevels = r.getDifficultyLevels();
		for(DifficultyLevel d : dLevels) {
			dl.put(d.getDifficulty()+" "+d.getLevel(), d);
		}
		ObservableList<String> dList=FXCollections.observableArrayList();
		for(String s : dl.keySet()) {
			dList.add(s);
		}
		riddleDiff.setItems(dList);
		ObservableList<String> rList=FXCollections.observableArrayList();
		riddles = new ArrayList<>();
		riddles = r.getRiddles();
		for(Riddle r : riddles) {
			if(r.getDifficultyLevel().getLevel() == 0)
				rList.add(r.getDiscription());
		}
		riddlesUnassigned.setItems(rList);
		unassignedDifficulty.setItems(dList);
		updateDifficulty.setItems(dList);
		// delete moudul
		levelToDelete.setItems(dList);
		riddlesByDis = new HashMap<>();
		for(Riddle r : riddles) {
			riddlesByDis.put(r.getDiscription(), r);
		}
		difiByDis = new HashMap<>();
		for(DifficultyLevel d : dLevels) {
			difiByDis.put(d.getDifficulty()+" "+d.getLevel(), d);
		}
		ExecuteLottery e = new ExecuteLottery();
		lotteries = new ArrayList<>();
		lotteries = e.getLotteries();
		ObservableList<String> lList=FXCollections.observableArrayList();
		for(Lottery l : lotteries) {
			lList.add(l.getLotteryId().toString());
		}
		loteryToDelete.setItems(lList);
		benefits = new ArrayList<>();
		benefits = e.getBenefits();
		ObservableList<String> bList=FXCollections.observableArrayList();
		for(Benefit b : benefits) {
			bList.add(b.getSerialNumber());
		}
		BenefitToDelete.setItems(bList);
		ArrayList<Riddle> riddles = new ArrayList<>();
		DeleteObject d = new DeleteObject();
		riddles = d.getRiddlesNotSolved();
		ObservableList<String> ridList=FXCollections.observableArrayList();
		for(Riddle r : riddles) {
			ridList.add(r.getDiscription());
		}
		riddleToDelete.setItems(ridList);
	}

	public void btnExport() {
		handler=new DataHandler();
		handler.exportTransactions();
		try {
			Verificator.getInsatnce().success();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void btnImport() {
		try {
			handler=new DataHandler();
			if(!handler.importTransactions() || handler.istExist())
			{
				Verificator.getInsatnce().importFail();
			}
			else
				Verificator.getInsatnce().success();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void chooseBlock()
	{

		Block b=new Block(blocksMyBlocks.getValue());

		b=blocks.get(blocks.indexOf(b));
		comb.setBlock(blocks.get(blocks.indexOf(b)));
		combinations=comb.getCombinations();
		ObservableList<String> combList=FXCollections.observableArrayList();
		for(Combination c:combinations)
		{
			combList.add(c.toString());
		}

		blocksTrans.setItems(combList);
		try {
			Verificator.getInsatnce().success();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateFeeSum()
	{
		if(blocksTrans.getValue() != null && !blocksTrans.getValue().isEmpty()) {
			String transaction = blocksTrans.getValue();
			String[] temp = transaction.split(",");
			comb.getTransactions();
			ArrayList<Transaction> transcations = new ArrayList<>();
			transcations = comb.getTransactions();
			Transaction a = transcations.get(transcations.indexOf(new Transaction(temp[0])));
			Transaction b = transcations.get(transcations.indexOf(new Transaction(temp[1])));
			Combination co = new Combination(a, b);
			blocksSum.setText(co.getSum().toString() + " Satoshis");
		}
	}

	public void insertBlock()
	{ try {

		if((blocksMyBlocks.getValue()==null ))
		{
			Verificator.getInsatnce().failTransAdd();
			return;
		}
		ArrayList<Transaction> transcations = new ArrayList<>();
		transcations = comb.getTransactions();
		HashMap<String, Transaction> tranById = new HashMap<>();
		for(Transaction t : transcations) {
			tranById.put(t.getId(), t);
		}
		Block b= new Block(blocksMyBlocks.getValue());
		Transaction t=new Transaction();
		t = tranById.get(blocksAddTrans.getValue());
		select= new TransactionSelection();
		select.relateTransactionBlock(t,b);
		refreshAfterInsertBlock();

		Verificator.getInsatnce().success();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	}

	public void refreshAfterInsertBlock() {
		ArrayList<Transaction> transact=comb.getTransactions();
		ObservableList<String> tList=FXCollections.observableArrayList();
		for(Transaction tr:transact)
		{
			tList.add(tr.getId());
		}
		blocksAddTrans.setItems(tList);
		blocksSum.setText("");
		combinations=comb.getCombinations();
		ObservableList<String> combList=FXCollections.observableArrayList();
		for(Combination c:combinations)
		{
			combList.add(c.toString());
		}

		blocksTrans.setItems(combList);
	}

	public void importRiddles() {
		ImportRiddles r = new ImportRiddles();
		if(r.importRiddles() && r.importSolutions())
			try {
				Verificator.getInsatnce().success();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else
			try {
				Verificator.getInsatnce().fail();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}



	}

	public void registerToRiddle()
	{
		r.registerToRiddle(LoginLogic.getMiner(), riddlesByDis.get(riddleRiddle.getText()));
		try {
			Verificator.getInsatnce().success();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void addDifficultyLevel()
	{
		u = new UpdateLevel();
		DifficultyLevel d = new DifficultyLevel(addDifficulty.getText(), Integer.parseInt(addLevel.getText()),
				Double.parseDouble(addBlockSize.getText()));
		if(!u.addLevel(d))
			try {
				Verificator.getInsatnce().fail();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		else
			try {
				Verificator.getInsatnce().success();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public void updateDifficulty()
	{
		u = new UpdateLevel();
		u.updateBlockSizeForLevel(difiByDis.get(updateDifficulty.getSelectionModel().getSelectedItem()),
				Double.parseDouble(updateBlockSize.getText()));
		try {
			Verificator.getInsatnce().success();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void assignDifficulty()
	{
		u = new UpdateLevel();
		u.updateLevel(riddlesByDis.get(riddlesUnassigned.getSelectionModel().getSelectedItem()),
				difiByDis.get(unassignedDifficulty.getSelectionModel().getSelectedItem()));
		try {
			Verificator.getInsatnce().success();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void displayRiddle() {
		riddleRiddle.setText(riddlesRiddle.getSelectionModel().getSelectedItem().toString());
		Date date = new Date();
		long l = r.getRiddleFinishDate(riddlesByDis.get(riddlesRiddle.getSelectionModel().getSelectedItem()).getId()).getTime() - date.getTime();
		String time = TimeUnit.HOURS.convert(l, TimeUnit.MILLISECONDS)+":"+TimeUnit.MINUTES.convert(l, TimeUnit.MILLISECONDS)%60;
		timeLeft.setText(time);
	}

	public void chooseLevel() {
		ObservableList<String> rList=FXCollections.observableArrayList();
		refreshCombo();
		ArrayList<Riddle> allRiddles = new ArrayList<>();
		ArrayList<Riddle> riddles = new ArrayList<>();
		ArrayList<SolvingMiner> solved = new ArrayList<>();
		HashSet<Integer> count = new HashSet<>();
		allRiddles = r.getRiddles();
		solved = r.getSolvingMiners();
		if(!solved.isEmpty()) {
			for(Riddle rid : allRiddles) {
				for(SolvingMiner s : solved) {
					if(LoginLogic.getMiner().equals(s.getMiner()) && s.getRiddle().getId().equals(rid.getId()) ||
							rid.getSolvingStatus().equals(Status.UNSOLVED))
					{
						riddles.add(rid);
					}
				}
			}
		}
		else
		{
			for(Riddle rid : allRiddles) {
				if(rid.getSolvingStatus().equals(Status.UNSOLVED)) {
					riddles.add(rid);
				}
			}
		}
		String toCheck = new String();
		for(Riddle r : riddles) {
			toCheck = r.getDifficultyLevel().getDifficulty() + " "+r.getDifficultyLevel().getLevel();
			if(!count.contains(r.getId()) && toCheck.equals(riddleDiff.getSelectionModel().getSelectedItem().toString())) {
				rList.add(r.getDiscription());
			}
			count.add(r.getId());
		}
		riddlesRiddle.setItems(rList);
		riddles.clear();
	}

	public void refreshCombo() {
		List<String> list = new ArrayList<>(riddlesRiddle.getItems());
		riddlesRiddle.getItems().removeAll(list);
	}


	public void sendSolution() {
		String solution = riddleSol.getText();
		Riddle riddle = new Riddle();
		riddle = riddlesByDis.get(riddleRiddle.getText());
		r = new SolveRiddle();
		try {
			if(r.solveRiddle(riddle, solution))
				try {
					if(riddle.getSolvingStatus().equals(Status.UNSOLVED)) {
						Block b = new Block(null);
						b = r.createBlock(difiByDis.get(riddleDiff.getSelectionModel().getSelectedItem()));
						refreshComboBlock(b);
						Verificator.getInsatnce().successSound();
						Alert al = new Alert(Alert.AlertType.INFORMATION);
						al.setContentText("You Are The First To Answer Correct!\n You Have A New Block!");
						al.setTitle("System Message");
						al.setResizable(false);
						al.showAndWait();
					}
					else {
						Verificator.getInsatnce().successSound();
						Alert al = new Alert(Alert.AlertType.INFORMATION);
						al.setContentText("Answer Is Correct!");
						al.setTitle("System Message");
						al.setResizable(false);
						al.showAndWait();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		} catch (RegisterException e) {
			try {
				Verificator.getInsatnce().errorSound();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				Verificator.getInsatnce().errorSound();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Alert al = new Alert(Alert.AlertType.ERROR);
			al.setContentText("You Need To Register To The Riddle First");
			al.setTitle("System Message");
			al.setResizable(false);
			al.showAndWait();
			e.printStackTrace();
		} catch (AnswerException e) {
			try {
				Verificator.getInsatnce().errorSound();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Alert al = new Alert(Alert.AlertType.ERROR);
			al.setContentText("The Answer Is Wrong, Try Again");
			al.setTitle("System Message");
			al.setResizable(false);
			al.showAndWait();
			e.printStackTrace();
		}
	}
	
	public void refreshComboBlock(Block b) {
		ObservableList<String> bList=FXCollections.observableArrayList();
		blocks.add(b);
		bList.add(b.getAddress());
		blocksMyBlocks.setItems(bList);
	}

	public void addBenefit() {
		CreateLottery c = new CreateLottery();
		if(c.defineBenefits(benefitToAdd.getText()))
			try {
				Verificator.getInsatnce().success();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else
			try {
				Verificator.getInsatnce().fail();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public void deleteBenefit() {
		DeleteObject d = new DeleteObject();
		if(d.deleteBenefit(BenefitToDelete.getSelectionModel().getSelectedItem()))
			try {
				Verificator.getInsatnce().success();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else
			try {
				Verificator.getInsatnce().fail();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public void deleteDifficultyLevel() {
		DeleteObject d = new DeleteObject();
		String[] dl = levelToDelete.getSelectionModel().getSelectedItem().split(" ");
		if(d.deleteDifficultyLevel(dl[0], Integer.parseInt(dl[1])))
			try {
				Verificator.getInsatnce().success();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else
			try {
				Verificator.getInsatnce().fail();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public void deleteLottery() {
		DeleteObject d = new DeleteObject();
		if(d.deleteLottery(Integer.parseInt(loteryToDelete.getSelectionModel().getSelectedItem())))
			try {
				Verificator.getInsatnce().success();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else
			try {
				Verificator.getInsatnce().fail();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public void deleteRiddle() {
		DeleteObject d = new DeleteObject();
		if(d.deleteRiddle(riddlesByDis.get(riddleToDelete.getSelectionModel().getSelectedItem()).getId()))
			try {
				Verificator.getInsatnce().success();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else
			try {
				Verificator.getInsatnce().fail();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public void updStatus() {
		UpdateIrreleventStatus u = new UpdateIrreleventStatus();
		if(u.updateIrreleventRiddles())
			try {
				Verificator.getInsatnce().success();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else
			try {
				Verificator.getInsatnce().fail();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}

