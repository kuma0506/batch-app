# プロジェクトについて
・Spring Batchのサンプルプロジェクト
・step.taskletのみ実装済み

# 概要
## Spring Batchの概念

### JobLauncher
・Jobを実行するエントリーポイントとなるインタフェースでJobの実行はrunメソッドを呼び出すことで行われる（runメソッドは実行するJobとJobParameterを受け取る）

### Job
・Spring Batchで一番大本にある要素でバッチ処理全体を表す。1Job = 1バッチ処理でJobは複数のStepから構成され、1つのJobを実行するとそれを構成する複数のStepが実行される

### JobParameter
・job実行時に渡すパラメータ。ここで渡したパラメータをJobやStepの変数として利用できる

### Step
・Chunkモデル
読み込み(ItemReader)、加工(ItemProcessor)、書き込み(ItemWriter)によって構成されるステップモデル。読み込み、加工、書き込みの順でStepが構成され、必ずそれぞれの処理が実行される。
（例えば読み込み、加工だけを行うようなStepにはできない）


・Taskletモデル
Chunkモデルとは違い、特に処理の流れが決まっておらず、自由に処理を記述できるステップモデル。何か単一処理を行う場合はこちらを利用する。

### JobRepository
・JobやStepの実行状況や実行結果を保存しておくところ。一般的にRDBなどのストレージで永続化させます

